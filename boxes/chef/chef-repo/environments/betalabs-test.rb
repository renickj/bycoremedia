name 'development'
description 'Boots BetaLab Development Environment'

#noinspection RubyStringKeysInHashInspection
override_attributes ({
                    'coremedia' => {
                        'logging' =>  { 'default' => {
                                        'com.coremedia' => {'level' => 'info'},
                                        'cap.server' => {'level' => 'info'},
                                        'hox.corem.server' => {'level' => 'info'},
                                        'workflow.server' => {'level' => 'info'}
                                      }},

                        'db' =>       { 'host' => '10.112.158.4' },

                        'content_archive' => %w(/opt/deploy/content/content-users.zip /opt/deploy/content/lc-content-users.zip),

                        'configuration' => {
                            # License configuration
                            'configure.CMS_LICENSE' =>  '/etc/coremedia/licenses/license-cms.zip',
                            'configure.MLS_LICENSE' =>  '/etc/coremedia/licenses/license-mls.zip',
                            'configure.RLS_LICENSE' =>  '/etc/coremedia/licenses/license-rls.zip',

                            # Database configuration
                            'configure.CMS_DB_URL' =>   'jdbc:mysql://10.112.158.4:3306/cm7mnmt_blt1',
                            'configure.CMS_DB_USER' =>      'cm7mnmt_blt1',
                            'configure.CMS_DB_PASSWORD' =>  'cm7mnmt_blt1',
                            'configure.MLS_DB_URL' =>   'jdbc:mysql://10.112.158.4:3306/cm7master_blt1',
                            'configure.MLS_DB_USER' =>      'cm7master_blt1',
                            'configure.MLS_DB_PASSWORD' =>  'cm7master_blt1',
                            'configure.CAEFEEDER_PREVIEW_DB_URL' => 'jdbc:mysql://10.112.158.4:3306/cm7mcaefeed_blt1',
                            'configure.CAEFEEDER_PREVIEW_DB_USER' =>      'cm7mcaefeed_blt1',
                            'configure.CAEFEEDER_PREVIEW_DB_PASSWORD' =>  'cm7mcaefeed_blt1',

                            'configure.CMS_SQL_SCHEMA_CREATE_DROP_INDEXES' => 'true',
                            'configure.CMS_SQL_SCHEMA_ALTER_TABLE' => 'true',
                            'configure.MLS_SQL_SCHEMA_CREATE_DROP_INDEXES' => 'true',
                            'configure.MLS_SQL_SCHEMA_ALTER_TABLE' => 'true',

                            # Delivery configuration
                            'configure.DELIVERY_REPOSITORY_HTTP_PORT' => '48080',
                            # use the master solr for delivery by default for this setup
                            'configure.DELIVERY_SOLR_PORT'=> '44080',

                            # Set MLS hostname
                            'configure.MLS_HOST' => 'mgmt1.cmtest.betalab.159.122.195.145.xip.io',

                            # Hostname configuration
                            'configure.STUDIO_TLD' => 'mgmt1.cmtest.betalab.159.122.195.145.xip.io',
                            'configure.WEBDAV_TLD' => 'mgmt1.cmtest.betalab.159.122.195.145.xip.io',
                            'configure.DELIVERY_TLD' => 'del1.cmtest.betalab.159.122.196.241.xip.io',

                            #segment configuration
                            'configure.STUDIO_HELIOS_SEGMENT' => 'studio',
                            'configure.PREVIEW_HELIOS_SEGMENT' => 'preview-by',
                            'configure.HELIOS_SEGMENT' => 'by',

                            # Studio configuration
                            'configure.STUDIO_PREVIEW_URL_WHITELIST' => '*.betalab.159.122.195.145.xip.io',

                            #MongoDB config, comma-separated mongo-hosts
                            'configure.MONGO_ADDRESSES' => 'mgmt1.cmtest.betalab.159.122.195.145.xip.io:27017',

                            # Misc
                            'configure.JAVA_HOME' =>    '/usr/lib/jvm/java'
                        }
                    },

                    'blueprint' => { 'yum' => { 'local' => { 'archive' => '' } } },

                    'java' => {
                         'install_flavor' => 'oracle',
                         'jdk_version' => '8',
                         'oracle' => { 'accept_oracle_download_terms' => true }
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