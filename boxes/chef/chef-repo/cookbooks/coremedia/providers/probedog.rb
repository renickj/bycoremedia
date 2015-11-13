#
# Cookbook Name:: coremedia
# Provider:: probedog
# This lightweigt provider (LWP) will probe against a service until a timeout is reached

require 'timeout'

def whyrun_supported?
  true
end

use_inline_resources

action :check do
  Chef::Log.debug("#{new_resource.tool_name} - executing Probedog #{new_resource.probe} with Timeout = #{new_resource.timeout} seconds")
  execute "#{new_resource.name}_#{new_resource.probe}" do
    command "#{node["coremedia"]["install_root"]}/#{new_resource.tool_name}#{cm_exec} probedog #{new_resource.probe}"
    user node["coremedia"]["user"] unless platform_family? 'windows'
    retry_delay 10
    retries (new_resource.timeout / 10).to_i
    timeout 30
  end
end
