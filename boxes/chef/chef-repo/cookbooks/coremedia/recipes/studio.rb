["STUDIO_PREVIEW_UAPI_BLOBCACHEPATH", "STUDIO_UAPI_BLOBCACHEPATH"].each do |option|
  blob_cache_dir = node["coremedia"]["configuration"]["configure.#{option}"] rescue nil
  if blob_cache_dir
    directory blob_cache_dir do
      recursive true
      unless platform_family?("windows")
        owner node["coremedia"]["user"]
        group node["coremedia"]["user"]
      end
    end
  end
end

coremedia_tool "cm7-cae-preview-tools"

coremedia_service "cm7-studio-tomcat"

coremedia_logging "cm7-studio-tomcat" do
  webapps %w(blueprint studio webdav elastic-worker)
  pattern '%d %-7([%level]) %logger [%X{tenant}] - %message \\\\(%thread\\\\)%n'
end

coremedia_probedog "probe_preview" do
  tool_name "cm7-cae-preview-tools"
  action :nothing
  probe "ProbeCae"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-studio-tomcat]", :delayed
end

coremedia_probedog "probe_studio" do
  tool_name "cm7-cae-preview-tools"
  action :nothing
  probe "ProbeStudio"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-studio-tomcat]", :delayed
end
