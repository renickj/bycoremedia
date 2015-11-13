coremedia_tool "cm7-cms-tools"

coremedia_service "cm7-cms-tomcat"

coremedia_logging "cm7-cms-tomcat" do
  webapps %w(coremedia contentfeeder user-changes)
end

coremedia_probedog "cm7-cms-tools" do
  action :nothing
  probe "ProbeContentServerOnline"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-cms-tomcat]", :delayed
  subscribes :check, "service[cm7-cms-tomcat_restart]", :delayed
end

working_directory = "#{node["coremedia"]["install_root"]}/data"

directory working_directory do
  unless platform_family?("windows")
    owner node["coremedia"]["user"]
    group node["coremedia"]["user"]
  end
  recursive true
end

unless platform_family?("windows")
  package "unzip" do
    retries node["coremedia"]["package"]["retries"]
  end
end