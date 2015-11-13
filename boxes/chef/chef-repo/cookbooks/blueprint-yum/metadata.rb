name             'blueprint-yum'
maintainer       ''
maintainer_email ''
license          ''
description      'Configures yum repositories'
long_description  IO.read(File.join(File.dirname(__FILE__), 'README.md'))
version          '0.1.0'
depends          'yum', '~> 3.6.0'
depends          'yum-centos', '~> 0.4.5'
depends          'yum-mysql-community', '~> 0.1.17'

supports 'redhat'
supports 'centos'
supports 'amazon'
