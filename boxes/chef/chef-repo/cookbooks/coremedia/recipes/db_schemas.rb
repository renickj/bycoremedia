if node["coremedia"]["db"]["type"] == "postgresql"
  include_recipe "postgresql::client"

  sql = "CREATE DATABASE coremedia WITH OWNER = postgres ENCODING = 'UTF8' CONNECTION LIMIT = -1;"
  execute "create database coremedia" do
    command "PGPASSWORD=#{node["postgresql"]["password"]["postgres"]} psql --host=#{node["coremedia"]["db"]["host"]} --user=postgres --no-password --command=\"#{sql}\""
    returns [0, 1]
  end

  node["coremedia"]["db"]["schemas"].each do |schema|
    sql = "CREATE ROLE #{schema} LOGIN PASSWORD '#{schema}' NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE; \
           CREATE SCHEMA #{schema} AUTHORIZATION #{schema};"
    execute "create #{schema} schema" do
      command "PGPASSWORD=#{node["postgresql"]["password"]["postgres"]} psql --host=#{node["coremedia"]["db"]["host"]} --dbname=coremedia --username=postgres --no-password --command=\"#{sql}\""
      not_if "PGPASSWORD=#{schema} psql --host=#{node["coremedia"]["db"]["host"]} --dbname=coremedia --username=#{schema} --no-password --command=''"
    end
  end

  sql = "REVOKE CREATE ON SCHEMA public FROM PUBLIC;"
  execute "revoke create on schema public" do
    command "PGPASSWORD=#{node["postgresql"]["password"]["postgres"]} psql --host=#{node["coremedia"]["db"]["host"]} --dbname=coremedia --user=postgres --no-password --command=\"#{sql}\""
  end
elsif node["coremedia"]["db"]["type"] == "sql_server"
  include_recipe "sql_server::client"

  node["coremedia"]["db"]["schemas"].each do |schema|
    template "#{ENV['TEMP']}/create_#{schema}.sql" do
      source "create_database_sql_server.sql.erb"
      variables(
              :schema => schema
      )
    end

    execute "create #{schema} schema" do
      command "\"#{node["coremedia"]["db"]["sql_server"]["sqlcmd"]}\" -i #{ENV['TEMP']}/create_#{schema}.sql"
      not_if "\"#{node["coremedia"]["db"]["sql_server"]["sqlcmd"]}\" -d #{schema} -Q \"SELECT GETDATE()\""
    end
  end
else
  include_recipe "mysql::client"

  node["coremedia"]["db"]["schemas"].each do |schema|
    sql = "CREATE SCHEMA #{schema} CHARACTER SET utf8mb4 COLLATE utf8mb4_bin; \
         GRANT ALL PRIVILEGES ON #{schema}.* TO '#{schema}'@'localhost' IDENTIFIED BY '#{schema}'; \
         GRANT ALL PRIVILEGES ON #{schema}.* TO '#{schema}'@'%' IDENTIFIED BY '#{schema}';"
    execute "create #{schema} schema" do
      command "mysql --host=#{node["coremedia"]["db"]["host"]} --user=root --password=\"#{node["mysql"]["server_root_password"]}\" --execute=\"#{sql}\""
      not_if "mysql --host=#{node["coremedia"]["db"]["host"]} --user=#{schema} --password=#{schema} --execute='' #{schema}"
    end
  end
end
