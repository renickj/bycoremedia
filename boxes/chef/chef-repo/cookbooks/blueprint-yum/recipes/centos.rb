#
# Cookbook Name:: blueprint-yum
# Recipe:: centos
#
# Copyright (C) 2014
#
#
if node['platform'] == 'centos'
  include_recipe 'yum-centos::default'
end

yum_globalconfig 'etc/yum.conf' do
  proxy 'http://172.18.112.55:80/'
end