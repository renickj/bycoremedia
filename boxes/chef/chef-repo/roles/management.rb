name "management"
description "The role for CoreMedia Management nodes"

#noinspection RubyStringKeysInHashInspection
override_attributes "coremedia" => {
                            "db" => {"schemas" => %w(cm7management cm7master cm7caefeeder cm7mcaefeeder)},
                    }

run_list "role[base]",
         "recipe[coremedia::db_schemas]",
         "recipe[mongodb]",
         "recipe[coremedia::solr_master]",
         "recipe[coremedia::master_live_server]",
         "recipe[coremedia::content_management_server]",
         "recipe[coremedia::workflow_server]",
         "recipe[coremedia::caefeeder_preview]",
         "recipe[coremedia::caefeeder_live]"
