name 'replication'
description 'The role for CoreMedia Replication nodes'

#noinspection RubyStringKeysInHashInspection
override_attributes(
    'coremedia' => {
        'configuration' => {
            'configure.RLS_SQL_SCHEMA_CREATE_DROP_INDEXES' => 'true',
            'configure.RLS_SQL_SCHEMA_ALTER_TABLE' => 'true',
            'configure.RLS_TMP_DIR' => '/var/cache/coremedia/cm7-rls-tomcat',
            'configure.SOLR_SLAVE_HEAP' => '128m',
            'configure.SOLR_SLAVE_PERM' => '64m',
            # use the rls for delivery by default for this setup
            'configure.DELIVERY_REPOSITORY_HTTP_PORT' => '48080',
            # use the slave solr for delivery by default for this setup
            'configure.DELIVERY_SOLR_PORT'=> '45080'
        }
    }
)

run_list 'role[management]',
         'recipe[coremedia::solr_slave]',
         'recipe[coremedia::replication_live_server]',
         'role[delivery]'
