#
# Cookbook Name:: blueprint-yum
# Recipe:: mysql-community
#
# Copyright (C) 2014
#
#
if node['blueprint']['yum']['mysql-community']['managed']
  include_recipe 'yum-mysql-community::mysql55'
end
