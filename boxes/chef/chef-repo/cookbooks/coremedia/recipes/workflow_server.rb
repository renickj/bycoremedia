coremedia_tool "cm7-wfs-tools"

coremedia_service "cm7-wfs-tomcat"

coremedia_logging "cm7-wfs-tomcat" do
  webapps "workflow"
end

coremedia_probedog "cm7-wfs-tools" do
  action :nothing
  probe "ProbeWorkflowServer"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-wfs-tomcat]", :delayed
end

coremedia_workflows "builtin_workflows" do
  action :nothing
  builtin_workflows node["coremedia"]["workflows"]["builtin"]
  wfs_tools "#{node["coremedia"]["install_root"]}/cm7-wfs-tools"
  subscribes :upload, "yum_package[cm7-wfs-tomcat]", :delayed
  subscribes :upload, "windows_zipfile[cm7-wfs-tomcat]", :delayed
  not_if { node["coremedia"]["workflows"]["builtin"].empty? }
end

unless node["coremedia"]["workflows"]["custom"].nil?
  node["coremedia"]["workflows"]["custom"].each_value do |workflow|
    coremedia_workflows workflow["definition"] do
      action :nothing
      definition workflow["definition"]
      jar workflow["jar"]
      wfs_tools "#{node["coremedia"]["install_root"]}/cm7-wfs-tools"
      subscribes :upload, "yum_package[cm7-wfs-tomcat]", :delayed
      subscribes :upload, "windows_zipfile[cm7-wfs-tomcat]", :delayed
      not_if { workflow['definition'].empty? }
    end
  end
end
