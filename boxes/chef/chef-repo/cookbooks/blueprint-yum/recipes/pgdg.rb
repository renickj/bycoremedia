#
# Cookbook Name:: blueprint-yum
# Recipe:: mysql-community
#
# Copyright (C) 2014
#
#
if node['blueprint']['yum']['pgdg']['managed']
  yum_repository 'pgdg' do
    description node['yum']['pgdg']['description']
    baseurl node['yum']['pgdg']['baseurl']
    mirrorlist node['yum']['pgdg']['mirrorlist']
    gpgcheck node['yum']['pgdg']['gpgcheck']
    gpgkey node['yum']['pgdg']['gpgkey']
    enabled node['yum']['pgdg']['enabled']
    repositoryid node['yum']['pgdg']['repositoryid']
    action :create
  end
end
