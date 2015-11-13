#
# Cookbook Name:: coremedia
# Resource:: workflows
#
# This lightweight (LWR) resource represents one ore multiple workflows to upload.

actions :upload
default_action :upload

attribute :username, :kind_of => String, :default => "admin"
attribute :password, :kind_of => String, :default => "admin"
attribute :wfs_tools, :kind_of => String, :default => "/opt/coremedia/wfs-tools"
attribute :builtin_workflows, :kind_of => Array, :default => []
attribute :definition, :kind_of => String, :default => ""
attribute :jar, :kind_of => String, :default => ""
attribute :timeout, :kind_of => Integer, :default => 500