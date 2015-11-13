name "mongodb-replicaset"
description "Role for a MongoDB replica set cluster"

#noinspection RubyStringKeysInHashInspection
override_attributes "mongodb" => {"cluster_name" => "coremedia"}

run_list "recipe[blueprint-yum::default]",
         "recipe[mongodb::10gen_repo]",
         "recipe[mongodb::replicaset]"
