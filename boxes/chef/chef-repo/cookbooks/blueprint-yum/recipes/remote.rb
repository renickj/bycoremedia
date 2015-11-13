#
# Cookbook Name:: blueprint-yum
# Recipe:: remote
#
# Copyright (C) 2014
#
#

if node['blueprint']['yum']['remote']['managed']
  yum_repository 'remote' do
    description 'remote Blueprint repository'
    baseurl node['blueprint']['yum']['remote']['baseurl']
    gpgcheck false
    enabled node['blueprint']['yum']['remote']['enabled']
    exclude node['blueprint']['yum']['remote']['exclude']
    enablegroups node['blueprint']['yum']['remote']['enablegroups']
    failovermethod 'priority'
    http_caching node['blueprint']['yum']['remote']['http_caching']
    includepkgs node['blueprint']['yum']['remote']['includepkgs']
    max_retries node['blueprint']['yum']['remote']['max_retries']
    metadata_expire node['blueprint']['yum']['remote']['metadata_expire']
    priority node['blueprint']['yum']['remote']['priority']
    proxy node['blueprint']['yum']['remote']['proxy']
    proxy_username node['blueprint']['yum']['remote']['proxy_username']
    proxy_password node['blueprint']['yum']['remote']['proxy_password']
    timeout node['blueprint']['yum']['remote']['timeout']
    action :create
  end
end
