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
                            'configure.MLS_DB_URL' =>   'jdbc:mysql://10.112.158.4:3306/cm7master_blt1',
                            'configure.CAEFEEDER_PREVIEW_DB_URL' => 'jdbc:mysql://10.112.158.4:3306/cm7mcaefeed_blt1',
                            'configure.CAEFEEDER_LIVE_DB_URL' => 'jdbc:mysql://10.112.158.4:3306/cm7caefeed_blt1',

                            # Delivery configuration
                            'configure.DELIVERY_REPOSITORY_HTTP_PORT' => '42080',
                            # use the master solr for delivery by default for this setup
                            'configure.DELIVERY_SOLR_PORT'=> '44080',

                            # Hostname configuration
                            'configure.STUDIO_TLD' => 'betalabs.test.mgmt1.boots.com',
                            'configure.WEBDAV_TLD' => 'betalabs.test.mgmt1.boots.com',
                            'configure.DELIVERY_TLD' => 'betalabs.test.del1.boots.com',

                            # Studio configuration
                            'configure.STUDIO_PREVIEW_URL_WHITELIST' => '*.boots.com',

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