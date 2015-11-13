#
# Cookbook Name:: coremedia
# Resource:: content
#
# This Lightweight resource (LWR) represents content to import and publish and if present also users to restore.

actions :unpack, :import, :bulkpublish
default_action :nothing

attribute :archive, :kind_of => Array
attribute :cms_tools, :kind_of => String, :default => "/opt/coremedia/cm7-cms-tools"
attribute :working_dir, :kind_of => String, :default => "/opt/coremedia/data"
attribute :content_folder, :kind_of => String, :default => "content"
attribute :users_folder, :kind_of => String, :default => "users"
attribute :username, :kind_of => String, :default => "admin"
attribute :password, :kind_of => String, :default => "admin"
attribute :timeout, :kind_of => Integer, :default => 1200
