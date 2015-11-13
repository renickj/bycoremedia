# uninstall yum or apt package for setuptools
package "python-setuptools" do
  action :purge
end

include_recipe 'python::default'
log_param = ""
node['psdash']['logs'].each do |log_dir|
  log_param << " -l \"#{log_dir}\" "
end

# upgrade setuptools before psdash installation to avoid "error: invalid command 'egg_info'"
python_pip 'setuptools' do
  action :upgrade
  version "latest"
end

python_pip 'psdash' do
  options '--allow-external argparse'
  version node['psdash']['version']
  notifies :restart, "service[psdash]", :delayed
end

template '/etc/init.d/psdash' do
  source 'psdash.erb'
  owner 'root'
  group 'root'
  mode 0744
  variables(
          :host => node['psdash']['host'],
          :port => node['psdash']['port'],
          :logs => log_param
  )
  notifies :restart, "service[psdash]", :delayed
end

service 'psdash' do
  supports :start => true, :stop => true, :restart => true, :status => true
  action [:enable, :start]
end