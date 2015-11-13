default['blueprint']['yum']['pgdg']['managed'] = false
default['yum']['pgdg']['version'] = '9.2'
version = node['yum']['pgdg']['version']

default['yum']['pgdg']['repositoryid'] = "pgdg-#{version}"
default['yum']['pgdg']['description'] = "PostgreSQL #{version}"
default['yum']['pgdg']['gpgkey'] = 'http://yum.postgresql.org/RPM-GPG-KEY-PGDG'
default['yum']['pgdg']['gpgcheck'] = true
default['yum']['pgdg']['enabled'] = true

default['yum']['pgdg']['baseurl'] = "http://yum.pgrpms.org/#{version}/redhat/rhel-$releasever-$basearch"