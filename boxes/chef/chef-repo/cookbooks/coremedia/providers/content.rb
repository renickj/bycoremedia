#
# Cookbook Name:: coremedia
# Provider:: content
#
#  This lightweight provider (LWP) provides the action to import and bulkpublish content and to restore users.
#  The provider needs the content and users in the form of a zip file, that will be extracted and imported,
#  if it has changed compared to the last imported state.
#

include Chef::Mixin::Checksum

use_inline_resources

action :unpack do
  require 'fileutils'

  @new_resource.archive.each do |zipfile|
    if ::File.exists?(zipfile)
      archive_target = "#{new_resource.working_dir}/#{::File.basename(zipfile)}"
      if !::File.exists?(archive_target) or checksum(zipfile) != checksum(archive_target)
        FileUtils.copy_file(zipfile, archive_target)
        if platform_family?("windows")
          windows_zipfile @new_resource.working_dir do
            source archive_target
            action :unzip
          end
        else
          FileUtils.chown(node["coremedia"]["user"], node["coremedia"]["user"], archive_target)
          `unzip -uo -qq #{archive_target} -d #{new_resource.working_dir}`
        end
        new_resource.updated_by_last_action(true)
      end
    else
      raise("#{new_resource.name} - content zip file not found at #{zipfile}")
    end
  end
end

action :import do
  coremedia_probedog "check-cms-before-import" do
    tool_name "cm7-cms-tools"
    action :check
    probe "ProbeContentServerOnline"
    timeout node["coremedia"]["probedog"]["timeout"]
  end

  import_content = execute "#{new_resource.name}_from_#{new_resource.working_dir}/#{new_resource.content_folder}" do
    user node["coremedia"]["user"] unless platform_family? 'windows'
    command "#{new_resource.cms_tools}#{cm_exec}  serverimport #{login_params} -r --no-validate-xml #{node["coremedia"]["serverimport"]["extra_options"]} #{new_resource.working_dir}/#{new_resource.content_folder}"
    retries 0
    # one hour is far more than any chef run should take
    timeout new_resource.timeout
    sensitive true
    only_if { ::File.exist?("#{new_resource.working_dir}/#{new_resource.content_folder}") }
  end
  new_resource.updated_by_last_action(import_content.updated_by_last_action?)

  if ::File.exists?("#{new_resource.working_dir}/#{new_resource.users_folder}")
    Dir.glob("#{new_resource.working_dir}/#{new_resource.users_folder}/*.xml") do |users_file|
      execute "#{new_resource.name}_from_#{users_file}" do
        user node["coremedia"]["user"] unless platform_family? 'windows'
        command "#{new_resource.cms_tools}#{cm_exec}  restoreusers #{login_params} -f #{users_file}"
        retries 0
        timeout new_resource.timeout
        sensitive true        
      end
      new_resource.updated_by_last_action(true)
    end
  else
    Chef::Log.info("#{new_resource.name} - No user definitions found at #{new_resource.users_folder} skip importing")
  end
end

action :bulkpublish do
  Chef::Log.info("#{new_resource.name} - bulkpublish content")
  coremedia_probedog 'check-cms-before-publish' do
    tool_name 'cm7-cms-tools'
    action :check
    probe 'ProbeContentServerOnline'
    timeout node["coremedia"]["probedog"]["timeout"]
  end
  coremedia_probedog 'check-mls-before-publish' do
    tool_name "cm7-mls-tools"
    action :check
    probe 'ProbeContentServerOnline'
    timeout node['coremedia']['probedog']['timeout']
  end

  execute "#{new_resource.name}_bulk_publish" do
    user node["coremedia"]["user"] unless platform_family? 'windows'
    command "#{new_resource.cms_tools}#{cm_exec} bulkpublish #{login_params} -a -b -c"
    retries 0
    sensitive true
    timeout new_resource.timeout
  end
  execute "#{new_resource.name}_bulk_publish_home" do
    user node["coremedia"]["user"] unless platform_family? 'windows'
    command "#{new_resource.cms_tools}#{cm_exec} bulkpublish #{login_params} -a -ub -f /Home"
    retries 0
    sensitive true
    timeout new_resource.timeout
  end
  new_resource.updated_by_last_action(true)
end

def login_params
  "-u #{new_resource.username} -p #{new_resource.password}"
end
