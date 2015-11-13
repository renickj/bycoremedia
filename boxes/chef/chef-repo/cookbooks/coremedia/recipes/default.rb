
java_home = (node['java'] && node['java']['java_home']) ? node['java']['java_home'] : nil
if java_home
  log "COREMEDIA - setting JAVA_HOME for user coremedia to #{java_home}"
  node.normal['coremedia']['configuration']['configure.JAVA_HOME'] = java_home
  log "COREMEDIA - setting configure.JAVA_HOME=#{node['coremedia']['configuration']['configure.JAVA_HOME']}"
end

unless platform? 'windows'

  include_recipe 'sysctl::apply'

  user node['coremedia']['user'] do
    supports :manage_home => true
    home node['coremedia']['install_root']
    comment 'CoreMedia Service User '
    shell '/bin/bash'
    system true
  end

  user_env_vars = Hash.new
  user_env_vars['JAVA_HOME'] = java_home if java_home
  user_env_vars = Chef::Mixin::DeepMerge.hash_only_merge!(user_env_vars, node['coremedia']['user_env_vars'])

  template "#{node['coremedia']['install_root']}/.bashrc" do
    source 'bashrc.erb'
    owner node['coremedia']['user']
    group node['coremedia']['user']
    mode 00644
    variables( :user_env_vars => user_env_vars)
  end

  user_ulimit node["coremedia"]["user"] do
    filehandle_limit node["coremedia"]["filehandle_limit"]
    process_limit node["coremedia"]["process_limit"]
  end
  # set alternatives for java and javac commands
  java_alternatives "set java alternatives" do
    java_location java_home
    bin_cmds ["java"]
    action :set
    only_if { java_home }
  end

end
