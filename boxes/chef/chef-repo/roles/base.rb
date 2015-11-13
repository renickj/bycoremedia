name 'base'
description 'The base role for CoreMedia nodes'

#noinspection RubyStringKeysInHashInspection
override_attributes 'java' => {'jdk_version' => '7'},
                    'blueprint' => {'yum' => {'local' => { 'archive' => 's3-eu-west-1://upload.cm7.coremedia.com/snapshot/com/coremedia/blueprint/boxes/11-SNAPSHOT/boxes-11-20121205.140127-7-rpms.zip'}}},
                    'mongodb' => {
                            'package_name' => 'mongodb-org',
                            'package_version' => '2.6.4-1',
                            'install_method' => 'custom-repo',
                            'cluster_name' => 'coremedia',
                            'config' => { 'rest' => true }
                    }

run_list 'recipe[blueprint-yum::default]',
         'recipe[java]'
