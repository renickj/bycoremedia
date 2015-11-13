name "storage"
description "The role for the storage layer"
#noinspection RubyStringKeysInHashInspection
override_attributes(
        "mysql" => {
                'client' => {
                        'packages' => ['mysql-community-client', 'mysql-community-devel']
                },
                'server' => {
                        'packages' => ['mysql-community-server']
                },
                "allow_remote_root" => "true",
                "bind_address" => "0.0.0.0",
                "server_root_password" => "coremedia",
                "server_repl_password" => "coremedia",
                "server_debian_password" => "coremedia",
                "tunable" => { "wait_timeout" => "7200" }
        },
        "coremedia" => {
                "db" => { "schemas" => %w(cm7management cm7master cm7replication cm7caefeeder cm7mcaefeeder) }
        },
        'mongodb' => {
                'package_name' => 'mongodb-org',
                'package_version' => '2.6.4-1',
                'install_method' => 'custom-repo',
                'config' => {
                        'rest' => true,
                        'smallfiles' => true,
                        'nojournal' => true
                }
        }
)
run_list "role[base]",
         "recipe[mysql::server]",
         "recipe[coremedia::db_schemas]",
         "recipe[mongodb]"
