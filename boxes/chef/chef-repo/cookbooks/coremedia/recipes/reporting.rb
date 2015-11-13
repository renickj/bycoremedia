include_recipe "chef_handler"

remote_directory node['chef_handler']['handler_path'] do
  source 'handlers'
  mode "0755"
  recursive true
  action :nothing
end.run_action(:create)

chef_handler "Coremedia::PackageUpdateReport" do
  source "#{node["chef_handler"]["handler_path"]}/package_updates_report.rb"
  action :nothing
end.run_action(:enable)

chef_handler "Coremedia::ElapsedTimeReport" do
  source "#{node["chef_handler"]["handler_path"]}/elapsed_time_report.rb"
  action :nothing
end.run_action(:enable)

chef_handler "Coremedia::MemoryReport" do
  source "#{node["chef_handler"]["handler_path"]}/memory_report.rb"
  action :nothing
end.run_action(:enable)