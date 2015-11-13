name "replication"
description "The role for CoreMedia Replication nodes"

#noinspection RubyStringKeysInHashInspection
override_attributes "coremedia" => {"db" => {"schemas" => %w(cm7replication)}}

run_list "role[base]",
         "recipe[coremedia::management_configuration_override]",
         "recipe[coremedia::db_schemas]",
         "recipe[coremedia::solr_slave]",
         "recipe[coremedia::replication_live_server]"
