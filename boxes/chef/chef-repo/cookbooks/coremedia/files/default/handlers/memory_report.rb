require 'rubygems'
require 'chef/handler'
require 'net/http'
require 'uri'
module Coremedia
  class MemoryReport < Chef::Handler

    def report
      credentials = jmx_proxy_credentials
      if credentials.nil?
        Chef::Log.warn("cannot create memory report as there is no tomcat user with role manager-jmx")
      else
        Chef::Log.info("")
        Chef::Log.info("Memory consumption report for all CoreMedia Services")
        Chef::Log.info("===================================================")
        tomcat_ports = {
                "cm7-studio-tomcat" => "40080",
                "cm7-cms-tomcat" => "41080",
                "cm7-mls-tomcat" => "42080",
                "cm7-wfs-tomcat" => "43080",
                "cm7-solr-master-tomcat" => "44080",
                "cm7-solr-slave-tomcat" => "45080",
                "cm7-caefeeder-preview-tomcat" => "46080",
                "cm7-caefeeder-live-tomcat" => "47080",
                "cm7-rls-tomcat" => "48080",
                "cm7-delivery-tomcat" => "49080"
        }
        tomcat_ports.each do |service, port|
          begin
            heap_memory = 0
            perm_memory = 0
            uri = ::URI.parse("http://localhost:#{port}/manager/jmxproxy/?get=java.lang:type=Memory&att=HeapMemoryUsage&key=used")
            request = Net::HTTP::Get.new uri.request_uri
            request.basic_auth credentials["username"], credentials["password"]
            Net::HTTP.start(uri.host, uri.port) do |http|
              response = http.request request
              if response.code == "200"
                heap_memory = response.body.split(" ").last.to_i / (1024 * 1024)
              end
            end
            uri = ::URI.parse("http://localhost:#{port}/manager/jmxproxy/?get=java.lang:type=Memory&att=NonHeapMemoryUsage&key=used")
            request = Net::HTTP::Get.new uri.request_uri
            request.basic_auth credentials["username"], credentials["password"]
            Net::HTTP.start(uri.host, uri.port) do |http|
              response = http.request request
              if response.code == "200"
                perm_memory = response.body.split(" ").last.to_i / (1024 * 1024)
              end
            end
            Chef::Log.info(" - #{service}: Heap / Perm = [#{heap_memory}MB / #{perm_memory}MB]")
          rescue Errno::ECONNREFUSED => e
            Chef::Log.debug("cannot access #{service} with url #{uri} on this node")
          rescue Errno::EAFNOSUPPORT => e
            Chef::Log.debug("cannot access #{service} with url #{uri} on this node")
          end
        end
        Chef::Log.info("")
      end
    end

    def jmx_proxy_credentials
      result = node["coremedia"]["tomcat"]["manager"]["credentials"].values.select { |e| e['roles']=~ /.*manager-jmx.*/ }
      unless result.empty?
        result.first
      end
    end
  end
end
