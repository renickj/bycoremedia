define :coremedia_apache_windows do
  package_name = params[:name]
  apache_install_dir = node["apache_windows"]["dir"] rescue "C:\\Apache2.2"

  directory node["coremedia"]["install_root"] do
    recursive true
  end

  directory node["coremedia"]["configure_root"] do
    recursive true
  end

  windows_zipfile package_name do
    source "#{node["coremedia"]["zip"]["dir"]}\\#{package_name}.zip"
    path "#{Chef::Config[:file_cache_path]}\\#{package_name}"
    not_if { ::File.exists?("#{Chef::Config[:file_cache_path]}\\#{package_name}\\install.bat") }
  end

  execute "install-#{package_name}" do
    action :nothing
    command "#{Chef::Config[:file_cache_path]}\\#{package_name}\\install.bat #{apache_install_dir}\\conf.d #{node["coremedia"]["configure_root"]}"
    subscribes :run, "windows_zipfile[#{package_name}]", :immediately
    notifies :restart, "service[#{node["coremedia"]["apache"]["service_name"]}]", :delayed
  end

  coremedia_configuration package_name do
    action :configure
    reconfigure_cmd "#{apache_install_dir}\\conf.d\\INSTALL\\postconfigure.bat #{node["coremedia"]["configure_root"]}"
    default_configuration "#{apache_install_dir}\\conf.d\\INSTALL\\#{package_name}.properties"
    notifies :restart, "service[#{node["coremedia"]["apache"]["service_name"]}]", :delayed
  end
end
