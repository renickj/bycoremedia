name "studio"
description "The role for CoreMedia studio services on Vagrant boxes"

override_attributes(
        "coremedia" => {
                "configuration" => {
                        "configure.STUDIO_HEAP" => "756m",
                        "configure.STUDIO_PERM" => "164m",
                        "configure.SITE_MANAGER_CMS_HTTP_PORT" => "80",
                        "configure.SITE_MANAGER_WFS_HTTP_PORT" => "80",
                        "configure.SITE_MANAGER_HTTP_PORT" => "80"
                }
        }
)
run_list "role[management]",
         "coremedia::studio",
         "coremedia::studio_apache",
         "coremedia::certificate_generator"
