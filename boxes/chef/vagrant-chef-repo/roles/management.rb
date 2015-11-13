name "management"
description "The role for CoreMedia management services on Vagrant boxes"
#noinspection RubyStringKeysInHashInspection
override_attributes(
        "coremedia" => {
                "configuration" => {
                        "configure.CMS_SQL_SCHEMA_CREATE_DROP_INDEXES" => "true",
                        "configure.CMS_SQL_SCHEMA_ALTER_TABLE" => "true",
                        "configure.MLS_SQL_SCHEMA_CREATE_DROP_INDEXES" => "true",
                        "configure.MLS_SQL_SCHEMA_ALTER_TABLE" => "true",
                        "configure.CMS_HEAP" => "256m",
                        "configure.CMS_PERM" => "128m",
                        # "configure.CMS_LICENSE" => "/shared/cms-license.zip",
                        "configure.MLS_HEAP" => "128m",
                        "configure.MLS_PERM" => "64m",
                        # "configure.MLS_LICENSE" => "/shared/mls-license.zip",
                        "configure.WFS_HEAP" => "96m",
                        "configure.WFS_PERM" => "64m",
                        "configure.CAEFEEDER_PREVIEW_HEAP" => "160m",
                        "configure.CAEFEEDER_PREVIEW_PERM" => "64m",
                        "configure.CAEFEEDER_LIVE_HEAP" => "160m",
                        "configure.CAEFEEDER_LIVE_PERM" => "64m",
                        "configure.SOLR_MASTER_HEAP" => "128m",
                        "configure.SOLR_MASTER_PERM" => "64m"
                }
                
        }
)
run_list "role[storage]",
         "coremedia::solr_master",
         "coremedia::master_live_server",
         "coremedia::content_management_server",
         "coremedia::workflow_server",
         "coremedia::caefeeder_preview",
         "coremedia::caefeeder_live"
