name "eds-delivery"
description "The role for LiveContext EDS Delivery nodes; RLS, Solr Master, CAE Feeder, CAE Live"

run_list "recipe[blueprint-yum::local]",
         "recipe[coremedia::solr_master]",
         "recipe[coremedia::replication_live_server]",
         "recipe[coremedia::caefeeder_live]",
         'recipe[coremedia::delivery]',
         'recipe[coremedia::sitemaps]'
