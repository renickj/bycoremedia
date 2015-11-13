#
# Cookbook Name:: coremedia
# Resource:: configuration
#
# This lightweight resource (LWR) represents the property file that configures the application after rpm installation.
# In case of a service, a "service XXX reconfigure", and in case of a tool a "<tool-dir>/reconfigure-tool.sh" is called.
#
# The resource checks if any chef property overrides a value from the default properties template installed. If chef overrides
# a default property, the change will be executed
# changes it and fires a change event.
#
# The property file should not be altered manually as chef will check its state only by matching

actions :configure
default_action :configure

attribute :application_name, :kind_of => String, :name_attribute => true
attribute :skip_reconfigure, :kind_of => [Integer, FalseClass], :default => false
attribute :reconfigure_cmd, :kind_of => String, :required => true
attribute :default_configuration, :kind_of => String
attribute :sensitive, :kind_of => [Integer, FalseClass], :default => false
