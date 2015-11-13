working_directory = "#{node["coremedia"]["install_root"]}/data"

coremedia_content "unpack-content" do
  action :unpack
  archive node["coremedia"]["content_archive"]
  working_dir working_directory
end

coremedia_content "import-content" do
  action :nothing
  working_dir working_directory
  cms_tools "#{node["coremedia"]["install_root"]}/cm7-cms-tools"
  subscribes :import, "coremedia_content[unpack-content]", :delayed
end

# this resource execution implies that the master_live_server recipe is executed before the content_management_server recipe
coremedia_content "publish-content" do
  action :nothing
  ignore_failure true
  cms_tools "#{node["coremedia"]["install_root"]}/cm7-cms-tools"
  subscribes :bulkpublish, "coremedia_content[import-content]", :delayed
end
