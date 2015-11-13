name "apache-only"
description "The role for CoreMedia apaches to run against CAE on host machine. (development only)"

override_attributes(
        "coremedia" => {
                "configuration" => {
                        "configure.STUDIO_HOST" => "192.168.252.1",
                        "configure.DELIVERY_HOST" => "192.168.252.1"
                }
        }
)
run_list  "recipe[blueprint-yum::default]",

          "coremedia::studio_apache",
          "coremedia::delivery_apache"

