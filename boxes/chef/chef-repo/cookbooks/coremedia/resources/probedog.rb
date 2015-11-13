#
# Cookbook Name:: coremedia
# Resource:: probedog
#
# This lightweight (LWR) resource represents a probedog check. It requires a tool with a probedog present. It will continue probing
# until the timeout is reached.

actions :check
default_action :check
attribute :probe, :kind_of => String, :required => true
attribute :tool_name, :kind_of => String, :name_attribute => true
attribute :timeout, :kind_of => Integer, :default => 300
