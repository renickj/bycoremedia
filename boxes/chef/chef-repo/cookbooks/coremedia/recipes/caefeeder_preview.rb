coremedia_tool "cm7-caefeeder-preview-tools"

coremedia_service "cm7-caefeeder-preview-tomcat"

coremedia_logging "cm7-caefeeder-preview-tomcat" do
  webapps "caefeeder"
end

coremedia_probedog "cm7-caefeeder-preview-tools" do
  action :nothing
  probe "ProbeCaeFeeder"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-caefeeder-preview-tomcat]", :delayed
end
