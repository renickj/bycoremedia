name 'delivery'
description 'The role for CoreMedia Delivery nodes'

#noinspection RubyStringKeysInHashInspection
override_attributes 'coremedia' => {'configuration' => {'configure.DELIVERY_TLD' => 'blueprint-ec2.coremedia.com'}}

run_list 'role[base]',
         'recipe[coremedia::management_configuration_override]',
         'recipe[coremedia::replication_configuration_override]',
         'recipe[coremedia::delivery]',
         'recipe[coremedia::delivery_apache]'
