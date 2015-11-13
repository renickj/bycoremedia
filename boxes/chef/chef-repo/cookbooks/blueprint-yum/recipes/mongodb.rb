#
# Cookbook Name:: blueprint-yum
# Recipe:: mongodb
#
# Copyright (C) 2014
#
#

if node['blueprint']['yum']['mongodb']['managed']
  yum_repository 'mongodb-repo' do
    description '10gen RPM Repository'
    baseurl node['blueprint']['yum']['mongodb']['baseurl']
    mirrorlist node['blueprint']['yum']['mongodb']['mirrorlist']
    gpgcheck node['blueprint']['yum']['mongodb']['gpgcheck']
    gpgkey node['blueprint']['yum']['mongodb']['gpgkey']
    enabled node['blueprint']['yum']['mongodb']['enabled']
    exclude node['blueprint']['yum']['mongodb']['exclude']
    enablegroups node['blueprint']['yum']['mongodb']['enablegroups']
    failovermethod 'priority'
    http_caching node['blueprint']['yum']['mongodb']['http_caching']
    include_config node['blueprint']['yum']['mongodb']['include_config']
    includepkgs node['blueprint']['yum']['mongodb']['includepkgs']
    max_retries node['blueprint']['yum']['mongodb']['max_retries']
    metadata_expire node['blueprint']['yum']['mongodb']['metadata_expire']
    mirror_expire node['blueprint']['yum']['mongodb']['mirror_expire']
    priority node['blueprint']['yum']['mongodb']['priority']
    proxy node['blueprint']['yum']['mongodb']['proxy']
    proxy_username node['blueprint']['yum']['mongodb']['proxy_username']
    proxy_password node['blueprint']['yum']['mongodb']['proxy_password']
    repositoryid 'mongodb'
    timeout node['blueprint']['yum']['mongodb']['timeout']
    action :create
  end
end
