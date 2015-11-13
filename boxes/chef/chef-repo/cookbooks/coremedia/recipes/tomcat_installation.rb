if platform? "windows"
  package_name = "cm7-tomcat-installation"

  directory node["coremedia"]["install_root"] do
    recursive true
  end

  windows_zipfile package_name do
    source "#{node["coremedia"]["zip"]["dir"]}\\#{package_name}.zip"
    path "#{Chef::Config[:file_cache_path]}\\#{package_name}"
    not_if { ::File.exists?("#{Chef::Config[:file_cache_path]}\\#{package_name}\\install.bat") }
  end

  execute "install-#{package_name}" do
    action :nothing
    command "#{Chef::Config[:file_cache_path]}\\#{package_name}\\install.bat #{node["coremedia"]["install_root"]}"
    subscribes :run, "windows_zipfile[#{package_name}]", :immediately
  end

  node["coremedia"]["tomcat"]["additional_jars"].each do |url|
    remote_file "#{node["coremedia"]["install_root"]}\\#{package_name}\\lib\\#{::File.basename(url)}" do
      source url
    end
  end
end
