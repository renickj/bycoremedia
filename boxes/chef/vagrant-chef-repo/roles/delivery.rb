name 'delivery'
description 'The role for CoreMedia delivery services on Vagrant boxes'
#noinspection RubyStringKeysInHashInspection
override_attributes(
        'coremedia' => {
                'configuration' => {
                        # use the mls for delivery by default for this setup
                        'configure.DELIVERY_REPOSITORY_HTTP_PORT' => '42080',
                        # use the master solr for delivery by default for this setup
                        'configure.DELIVERY_SOLR_PORT'=> '44080',
                        'configure.DELIVERY_HEAP' => '384m',
                        'configure.DELIVERY_PERM' => '92m'
                }
        }
)
run_list 'role[studio]',
         'coremedia::delivery',
         'coremedia::delivery_apache'
