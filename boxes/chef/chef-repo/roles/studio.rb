name "studio"
description "The role for CoreMedia Studio nodes"

#noinspection RubyStringKeysInHashInspection
override_attributes "coremedia" => {"configuration" => {"configure.STUDIO_TLD" => "blueprint-ec2.coremedia.com",
                                                        "configure.WEBDAV_TLD" => "blueprint-ec2.coremedia.com"}}

run_list "role[base]",
         "recipe[coremedia::management_configuration_override]",
         "recipe[coremedia::studio]",
         "recipe[coremedia::studio_apache]"
