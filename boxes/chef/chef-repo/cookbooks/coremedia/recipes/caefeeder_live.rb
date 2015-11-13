coremedia_tool "cm7-caefeeder-live-tools"

coremedia_service "cm7-caefeeder-live-tomcat"

coremedia_logging "cm7-caefeeder-live-tomcat" do
  webapps "caefeeder"
end

coremedia_probedog "cm7-caefeeder-live-tools" do
  action :nothing
  probe "ProbeCaeFeeder"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-caefeeder-live-tomcat]", :delayed
end
