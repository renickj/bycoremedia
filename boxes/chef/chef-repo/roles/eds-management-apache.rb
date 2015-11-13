name "eds-management-apache"
description "The role for LiveContext EDS Management nodes; CMS, WFS, MLS, Studio, Preview"

run_list "recipe[blueprint-yum::local]",
         "recipe[coremedia::db_schemas]",
         "recipe[mongodb]",
         "recipe[coremedia::solr_master]",
         "recipe[coremedia::master_live_server]",
         "recipe[coremedia::content_management_server]",
         "recipe[coremedia::workflow_server]",
         "recipe[coremedia::caefeeder_preview]",
         "recipe[coremedia::studio]",
         "recipe[coremedia::studio_apache]"