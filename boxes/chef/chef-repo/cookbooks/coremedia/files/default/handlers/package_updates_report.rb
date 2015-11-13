require 'rubygems'
require 'chef/handler'
module Coremedia
  class PackageUpdateReport < Chef::Handler
    def report
      updated_package_resources = run_status.updated_resources.select { |r| "package" == r.resource_name.to_s || "yum_package" == r.resource_name.to_s }
      updated_package_names = SortedSet.new
      updated_package_resources.each do |r|
        updated_package_names.add("#{r.package_name} - version now installed: #{r.version}")
      end
      unless updated_package_names.empty?
        Chef::Log.info ""
        Chef::Log.info "Updated packages:"
        updated_package_names.each do |p|
          Chef::Log.info " #{p}"
        end
        Chef::Log.info ""
      end
    end
  end
end
