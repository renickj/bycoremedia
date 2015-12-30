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