define :coremedia_apache_unix do
  package_name = params[:name]
  initial_install = !(system("rpm -qi #{package_name} > /dev/null"))
  version_to_install = node["coremedia"]["version"]["#{package_name}"].nil? ? node["coremedia"]["version"]["global"] : node["coremedia"]["version"]["#{package_name}"]


  if initial_install
    # on initial run we don't need to trigger restarts on the service
    if version_to_install.nil?
      # install latest version
      yum_package "#{package_name}" do
        retries node["coremedia"]["package"]["retries"]
        action :upgrade
      end
    else
      # install specific version
      yum_package "#{package_name}" do
        retries node["coremedia"]["package"]["retries"]
        version version_to_install
        action :install
      end
    end

    coremedia_configuration "#{package_name}" do
      action :configure
      reconfigure_cmd "/etc/httpd/conf.d/INSTALL/reconfigure.sh #{package_name} /etc/httpd/conf.d"
      default_configuration "/etc/httpd/conf.d/INSTALL/#{package_name}.properties"
      notifies :restart, "service[httpd]", :delayed
    end

  else
    # now we need to trigger restarts on service
    if version_to_install.nil?
      # install latest version
      yum_package "#{package_name}" do
        retries node["coremedia"]["package"]["retries"]
        action :upgrade
        notifies :restart, "service[httpd]", :delayed
      end
    else
      # install specific version and allow downgrade
      yum_package "#{package_name}" do
        retries node["coremedia"]["package"]["retries"]
        version version_to_install
        action :install
        allow_downgrade true
        notifies :restart, "service[httpd]", :delayed
      end
    end

    coremedia_configuration "#{package_name}" do
      action :configure
      reconfigure_cmd "/etc/httpd/conf.d/INSTALL/reconfigure.sh #{package_name} /etc/httpd/conf.d"
      default_configuration "/etc/httpd/conf.d/INSTALL/#{package_name}.properties"
      notifies :restart, "service[httpd]", :delayed
    end
  end

  service "httpd" do
    supports :start => true, :stop => true, :restart => true, :status => true, :reload => true
    action [:enable, :start]
  end
end

