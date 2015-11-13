define :coremedia_logging, :webapps => %w(coremedia) do
  package_name = params[:name]
  webapps = params[:webapps]
  pattern = params[:pattern]

  unless webapps.kind_of?(Array)
    webapps = [webapps]
  end

  webapps.each do |webapp|
    template "#{node["coremedia"]["install_root"]}/#{package_name}/webapps/#{webapp}/WEB-INF/logback.xml" do
      source "logback_config.erb"
      unless platform_family?("windows")
        owner node["coremedia"]["user"]
        mode "0644"
      end
      variables({:logger => node["coremedia"]["logging"][package_name].nil? ?
              node["coremedia"]["logging"]["default"] :
              node["coremedia"]["logging"][package_name],
                 :pattern => pattern
                })
    end
  end
end
