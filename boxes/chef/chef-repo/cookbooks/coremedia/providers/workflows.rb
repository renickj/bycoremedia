#
# Cookbook Name:: coremedia
# Provider:: workflows
#
#  This lightweight provider (LWP) provides the action to upload one ore multiple workflows
use_inline_resources

action :upload do
  coremedia_probedog "check-wfs-before-import-#{new_resource.name}" do
    tool_name "cm7-wfs-tools"
    action :check
    probe "ProbeWorkflowServer"
    timeout 30
  end

  if !new_resource.builtin_workflows.empty? && new_resource.definition.empty?
    upload_log = "#{new_resource.name} - Uploading built-in workflows #{new_resource.builtin_workflows}"
    upload_params = "-n #{new_resource.builtin_workflows.join(' ')}"
  elsif !new_resource.definition.empty? && new_resource.builtin_workflows.empty?
    upload_params = "-f #{new_resource.definition}"
    upload_log = "#{new_resource.name} - Uploading custom workflow from processdefinition #{new_resource.definition}"
    unless new_resource.jar.empty?
      upload_params << " -j #{new_resource.jar}"
      upload_log << " together with jar #{new_resource.jar}"
    end
  else
    Chef::Log.error("You cannot upload workflows using the 'builtin_workflows' and 'definition' attributes at the same time")
  end

  Chef::Log.info(upload_log)
  execute "#{new_resource.name}" do
    user node["coremedia"]["user"] unless platform_family? 'windows'
    command "#{new_resource.wfs_tools}#{cm_exec} upload -u #{new_resource.username} -p #{new_resource.password} #{upload_params}"
    retry_delay 10
    retries 2
    timeout new_resource.timeout
    sensitive true
  end
  new_resource.updated_by_last_action(true)
end