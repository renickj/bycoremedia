name "coremedia"
maintainer "CoreMedia AG"
description "Performs setup tasks for CoreMedia applications"
long_description IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version "1.0.1"

depends 'chef_handler', '= 1.2.0'
depends 'mysql', '= 4.0.20'
depends 'postgresql', '= 3.4.12'
depends 'ulimit', '= 0.3.2'
depends 'java', '= 1.27.0'
depends 'sql_server', '= 1.2.2'
depends 'cron', '= 1.6.1'
depends 'sysctl', '0.6.2'
suggests 'mongodb', '= 0.16.2'
suggests 'windows', '1.34.8'

%w{ redhat centos amazon}.each do |os|
  supports os
end
