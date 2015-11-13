coremedia_tool "cm7-rls-tools"

coremedia_service "cm7-rls-tomcat"

coremedia_logging "cm7-rls-tomcat"

coremedia_probedog "cm7-rls-tools" do
  action :nothing
  probe "ProbeContentServerOnline"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-rls-tomcat]", :delayed
end
