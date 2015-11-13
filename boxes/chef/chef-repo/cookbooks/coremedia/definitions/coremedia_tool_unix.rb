define :coremedia_tool_unix do
  package_name = params[:name]
  version_to_install = node["coremedia"]["version"]["#{package_name}"].nil? ? node["coremedia"]["version"]["global"] : node["coremedia"]["version"]["#{package_name}"]

  if version_to_install.nil?
    # install latest version
    yum_package "#{package_name}" do
      retries node["coremedia"]["package"]["retries"]
      action :upgrade
    end
  else
    # install specific version and allow downgrade
    yum_package "#{package_name}" do
      retries node["coremedia"]["package"]["retries"]
      version version_to_install
      action :install
      allow_downgrade true
    end
  end

  coremedia_configuration package_name do
    action :configure
    reconfigure_cmd "#{node["coremedia"]["install_root"]}/#{package_name}/reconfigure-tool.sh -y"
  end
end
