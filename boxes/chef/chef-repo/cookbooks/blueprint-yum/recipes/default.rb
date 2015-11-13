#
# Cookbook Name:: blueprint-yum
# Recipe:: default
#
# Copyright (C) 2014
#
#

include_recipe 'yum::default'
include_recipe 'blueprint-yum::centos'
include_recipe 'blueprint-yum::pgdg'
include_recipe 'blueprint-yum::mongodb'
include_recipe 'blueprint-yum::local'
include_recipe 'blueprint-yum::remote'
