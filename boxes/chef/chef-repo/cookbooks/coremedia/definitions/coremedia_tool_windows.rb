define :coremedia_tool_windows do
  package_name = params[:name]

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
    command "#{Chef::Config[:file_cache_path]}\\#{package_name}\\install.bat  #{node["coremedia"]["install_root"]} #{node["coremedia"]["configure_root"]}"
    subscribes :run, "windows_zipfile[#{package_name}]", :immediately
  end

  coremedia_configuration package_name do
    action :configure
    reconfigure_cmd "#{node["coremedia"]["install_root"]}\\#{package_name}\\INSTALL\\postconfigure.bat #{node["coremedia"]["configure_root"]}"
  end
end
