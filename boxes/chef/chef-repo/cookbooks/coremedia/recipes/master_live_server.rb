coremedia_tool "cm7-mls-tools"

coremedia_service "cm7-mls-tomcat"

coremedia_logging "cm7-mls-tomcat"

coremedia_probedog "cm7-mls-tools" do
  action :nothing
  probe "ProbeContentServerOnline"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-mls-tomcat]", :delayed
end
