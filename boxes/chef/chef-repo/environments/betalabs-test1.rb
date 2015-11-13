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
                            'configure.CMS_DB_URL' =>       'jdbc:mysql://10.112.158.4:3306/cm7management_t1',
                            'configure.CMS_DB_USER' =>      'cm7management_t1',
                            'configure.CMS_DB_PASSWORD' =>  'cm7management_t1',

                            'configure.MLS_DB_URL' =>       'jdbc:mysql://10.112.158.4:3306/cm7master_t1',
                            'configure.MLS_DB_USER' =>      'cm7master_t1',
                            'configure.MLS_DB_PASSWORD' =>  'cm7master_t1',

                            'configure.CAEFEEDER_PREVIEW_DB_URL' =>       'jdbc:mysql://10.112.158.4:3306/cm7mcaefeeder_t1',
                            'configure.CAEFEEDER_PREVIEW_DB_USER' =>      'cm7mcaefeeder_t1',
                            'configure.CAEFEEDER_PREVIEW_DB_PASSWORD' =>  'cm7mcaefeeder_t1',

                            # use the master solr for delivery by default for this setup
                            'configure.DELIVERY_SOLR_PORT'=> '44080',

                            # Set MLS hostname
                            'configure.MLS_HOST' => '159.122.196.228',

                            # Hostname configuration
                            'configure.STUDIO_TLD' => '159.122.196.228',
                            'configure.WEBDAV_TLD' => '159.122.196.228',
                            'configure.STUDIO_HELIOS_SEGMENT' => 'www.studio',

                            # Studio configuration
                            'configure.STUDIO_PREVIEW_URL_WHITELIST' => '*.cms.boots.com, *.int.boots.com, http://159.122.196.228:*',
                            'configure.HELIOS_SEGMENT' => '',
                            'configure.DELIVERY_TLD' => '159.122.196.228',


                            # Misc
                            'configure.JAVA_HOME' =>    '/usr/lib/jvm/java',

                            # LiveContext configuration
                            'configure.LIVECONTEXT_SERVICE_USERNAME' => 'cmsadmin',
                            'configure.LIVECONTEXT_SERVICE_PASSWORD' => 'cmsadm1n',
                            'configure.LIVECONTEXT_WCS_URL' => 'http://www.sandpitestore.int.boots.com',
                            'configure.LIVECONTEXT_WCS_SECURE_URL' => 'https://www.sandpitestore.int.boots.com',
                            'configure.LIVECONTEXT_WCS_VERSION' => '7.8',
                            'configure.LIVECONTEXT_HOST' => 'www.sandpitestore.int.boots.com',
                            'configure.LIVECONTEXT_WCS_STOREFRONT_PREVIEW_URL' => '//www.sandpitestore.int.boots.com/webapp/wcs/stores/servlet/',
                            'configure.LIVECONTEXT_WCS_REST_SEARCH_URL' => 'http://www.sandpitestore.int.boots.com:3737/search/resources',
                            'configure.LIVECONTEXT_WCS_REST_SEARCH_SECURE_URL' => 'http://www.sandpitestore.int.boots.com:3737/search/resources',
                            'configure.LIVECONTEXT_WCS_URL_KEYWORD' => 'cm',
                            'configure.AURORA_STORE_NAME' => 'eBoots_UK',
                            'configure.LIVECONTEXT_COOKIE_DOMAIN' => 'cms.boots.com',
                            'configure.AURORA_CURRENCY' => 'GBP',

                            #SiteManger configuration
                            'configure.SITE_MANAGER_HOST_NAME' => '159.122.196.228',
                            'configure.SITE_MANAGER_HTTP_PORT' => '40080',
                            'configure.SITE_MANAGER_CMS_HOST' => '159.122.196.228',
                            'configure.SITE_MANAGER_CMS_HTTP_PORT' => '41080'

                        },

                        'sitemaps' => {
                            'helios'  => {
                                'path'  => '/Sites/PerfectChef/United\%20States/English',
                                'hour' => '*/12'
                            }
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