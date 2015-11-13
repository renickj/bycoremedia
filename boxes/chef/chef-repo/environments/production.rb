name 'development'
description 'Boots BetaLab Development Environment'

default_attributes(
        "chef_client" => {
                "config" => {
                        'http_proxy' => 'http://172.18.112.55:80/',
                        'https_proxy' => 'http://172.18.112.55:80/'
                }
        }
)

#noinspection RubyStringKeysInHashInspection
override_attributes ({
                            'coremedia' => {
                                    'logging' => {'default' => {
                                            'com.coremedia' => {'level' => 'info'},
                                            'cap.server' => {'level' => 'info'},
                                            'hox.corem.server' => {'level' => 'info'},
                                            'workflow.server' => {'level' => 'info'}
                                    }},

                                    'db' => {'host' => '10.112.158.4'},

                                    'content_archive' => %w(/opt/deploy/content/content-users.zip /opt/deploy/content/content-users.zip),

                                    'configuration' => {
                                            # License configuration
                                            'configure.CMS_LICENSE' => '/etc/coremedia/licenses/license-cms.zip',
                                            'configure.MLS_LICENSE' => '/etc/coremedia/licenses/license-mls.zip',
                                            'configure.RLS_LICENSE' => '/etc/coremedia/licenses/license-rls.zip',

                                            # Database configuration
                                            'configure.CMS_DB_DRIVER' => 'oracle.jdbc.driver.OracleDriver',
                                            'configure.CMS_DB_URL' => 'jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(LOAD_BALANCE = yes)(FAILOVER = ON)(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.244)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.245)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.246)(PORT = 1521)))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = BDCPRDCM)))',
                                            'configure.CMS_DB_USER' => 'PRDCMCMS',
                                            'configure.CMS_DB_PASSWORD' => 'Chem16ts',

                                            'configure.MLS_DB_DRIVER' => 'oracle.jdbc.driver.OracleDriver',
                                            'configure.MLS_DB_URL' => 'jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(LOAD_BALANCE = yes)(FAILOVER = ON)(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.244)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.245)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.246)(PORT = 1521)))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = BDCPRDCM)))',
                                            'configure.MLS_DB_USER' => 'PRDCMMLS',
                                            'configure.MLS_DB_PASSWORD' => 'Chem16ts',

                                            'configure.CAEFEEDER_PREVIEW_DB_DRIVER' => 'oracle.jdbc.driver.OracleDriver',
                                            'configure.CAEFEEDER_PREVIEW_DB_URL' => 'jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS_LIST =(LOAD_BALANCE = yes)(FAILOVER = ON)(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.244)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.245)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = 10.139.13.246)(PORT = 1521)))(CONNECT_DATA =(SERVER = DEDICATED)(SERVICE_NAME = BDCPRDCM)))',
                                            'configure.CAEFEEDER_PREVIEW_DB_USER' => 'PRDCMFEP',
                                            'configure.CAEFEEDER_PREVIEW_DB_PASSWORD' => 'Chem16ts',

                                            # use the master solr for delivery by default for this setup
                                            'configure.DELIVERY_SOLR_PORT' => '44080',

                                            # Set MLS hostname
                                            'configure.MLS_HOST' => '10.139.3.180',

                                            # Hostname configuration
                                            'configure.STUDIO_TLD' => 'BDCPRDCMM01.cms.boots.com',
                                            'configure.WEBDAV_TLD' => 'BDCPRDCMM01.cms.boots.com',
                                            'configure.DELIVERY_TLD' => '.cms.boots.com',

                                            # Studio configuration
                                            'configure.STUDIO_PREVIEW_URL_WHITELIST' => '*.cms.boots.com',

                                            # Misc
                                            'configure.JAVA_HOME' => '/usr/lib/jvm/java'
                                    },

                                    'sitemaps' => {
                                            'helios' => {
                                                    'path' => '/Sites/PerfectChef/United\%20States/English',
                                                    'hour' => '*/12'
                                            }
                                    }
                            },

                            'blueprint' => {'yum' => {'local' => {'archive' => ''}}},

                            'java' => {
                                    'install_flavor' => 'oracle',
                                    'jdk_version' => '8',
                                    'oracle' => {'accept_oracle_download_terms' => true}
                            },

                            'mysql' => {
                                    'server_root_password' => 'yEyff}Ic)o'
                            },

                            'mongodb' => {
                                    'package_name' => 'mongodb-org',
                                    'package_version' => '2.6.9-1',
                                    'enable_rest' => true
                            }
                    })
