# the version of the rpms to deploy
default['coremedia']['version']['global'] = nil
# the directory you configured with CONFIGURE_ROOT in the maven build
default["coremedia"]["configure_root"] = "/etc/coremedia"
# the directory you configured with INSTALL_ROOT in the maven build
default["coremedia"]["install_root"] = "/opt/coremedia"
# the user you configured with INSTALL_USER in the maven build
default["coremedia"]["user"] = "coremedia"
# a hash of environment variables that need to be set for the services processes and the service user
# this is not working for windows platforms
# this can be used to set a specific java version i.e.
# default['coremedia']['user_env_vars']['JAVA_HOME'] = '/usr/java/jdk1.8.0_25'
default['coremedia']['user_env_vars'] = {}

# the path to the directory containing the CoreMedia ZIP artifacts
default["coremedia"]["zip"]["dir"] = "/shared/zip-repo"
# the URL from which to retrieve archived CoreMedia ZIP artifacts
default["coremedia"]["zip"]["archive_url"] = ""

# file handle limit for the coremedia user
default["coremedia"]["filehandle_limit"] = 25000
# process limit for the coremedia user
default["coremedia"]["process_limit"] = 5000

# default workflows to upload
default["coremedia"]["workflows"]["builtin"] = %w(studio-simple-publication.xml immediate-publication.xml studio-two-step-publication.xml three-step-publication.xml global-search-replace.xml /com/coremedia/translate/workflow/derive-site.xml)

# to upload custom workflows, you need to define a hash with a key definition and can have a key jar below the ["coremedia"]["workflows"]["custom"] key e.g.
#default["coremedia"]["workflows"]["custom"]["my-workflow"]["definition"] = "/opt/coremedia/my-workflow.xml"
#default["coremedia"]["workflows"]["custom"]["my-workflow"]["jar"] = "/opt/coremedia/my-workflow.jar"
default["coremedia"]["workflows"]["custom"]["translation"]["definition"] = "/opt/coremedia/cm7-wfs-tools/properties/corem/workflows/translation.xml"

# the path of the zipped content
# default["coremedia"]["content_archive"] = ["/shared/content/content-users.zip", "/shared/content/lc-content-users.zip", "/shared/content/corporate-content-users.zip"]
default["coremedia"]["content_archive"] = ["/shared/content/content-users.zip"]

# schemas to create on a node
default["coremedia"]["db"]["schemas"] = []

# database host
default["coremedia"]["db"]["host"] = "localhost"

# database type (currently either mysql or postgresql)
default["coremedia"]["db"]["type"] = "mysql"

if node['sql_server']
  default["coremedia"]["db"]["sql_server"]["sqlcmd"] = "#{node['sql_server']['install_dir']}\\100\\Tools\\Binn\\sqlcmd"
end

# login credentials for the tomcat manager app
default["coremedia"]["tomcat"]["manager"]["credentials"] = {}

# an array of URLs to download and add to the Tomcat lib directory
default["coremedia"]["tomcat"]["additional_jars"] = []

default["coremedia"]["configuration"]["configure.CAEFEEDER_LIVE_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.CAEFEEDER_PREVIEW_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.CMS_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.CMS_IP"] = node["ipaddress"]
default["coremedia"]["configuration"]["configure.DELIVERY_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.DELIVERY_REPOSITORY_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.DELIVERY_SOLR_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.DELIVERY_SOLR_PORT"] = "45080"
default["coremedia"]["configuration"]["configure.MLS_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.MLS_IP"] = node["ipaddress"]
default["coremedia"]["configuration"]["configure.RLS_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.RLS_IP"] = node["ipaddress"]
default["coremedia"]["configuration"]["configure.SOLR_MASTER_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.SOLR_SLAVE_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.STUDIO_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.WEBDAV_TLD"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.WEBDAV_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.WFS_HOST"] = node["fqdn"]
default["coremedia"]["configuration"]["configure.WFS_IP"] = node["ipaddress"]
default["coremedia"]["configuration"]["configure.CSS_IMPORT_INBOX_DIR"] = "/opt/coremedia/css-importer-inbox"
default["coremedia"]["configuration"]["configure.CAE_VIEW_DEBUG_ENABLED"] = "false"
default["coremedia"]["configuration"]["configure.FREEMARKER_PREVIEW_TEMPLATE_CACHE_UPDATE_DELAY_IN_SECONDS"] = 5
default["coremedia"]["configuration"]["configure.FREEMARKER_DELIVERY_TEMPLATE_CACHE_UPDATE_DELAY_IN_SECONDS"] = 60

# default logger profiles
# unless you define something like default["coremedia"]["logging"]["<PACKAGE NAME>"] the default logging config will be used
default["coremedia"]["logging"]["default"]["com.coremedia"]["level"] = "warn"
default["coremedia"]["logging"]["default"]["cap.server"]["level"] = "warn"
default["coremedia"]["logging"]["default"]["hox.corem.server"]["level"] = "warn"
default["coremedia"]["logging"]["default"]["workflow.server"]["level"] = "warn"
# cap.server has to be logged at least at level info so that the replicator status can be detected
default["coremedia"]["logging"]["cm7-rls-tomcat"]["cap.server"]["level"] = "info"

# specifies the number of times to retry the installation of packages
default["coremedia"]["package"]["retries"] = 3
# allow configuration of probedog timeout
default["coremedia"]["probedog"]["timeout"] = 300

# name of the roles to search for in the management_configuration_override and replication_configuration_override recipes
default["coremedia"]["role"]["management"] = "management"
default["coremedia"]["role"]["replication"] = "replication"
default["coremedia"]["role"]["delivery"] = "delivery"

default["coremedia"]["apache"]["service_name"] = "Apache2.2"

default["coremedia"]["serverimport"]["extra_options"] = ""

# EXTRA CATALINA_OPTS
# For each tomcat you can set extra CATALINA_OPTS using the following attribute structure:
# default["coremedia"]["catalina_opts"]["<package_name"] = "someopts"

# To enable JVM debugging, you can set debug options for each tomcat using the catalina_opts attribute. I.e. if you want to debug the studio-tomcat,
# you need to set the following node attribute, but make sure the port, here 5005, is not already used.
# To unset debugging, simply remove the attribute, the service will restart automatically.
# ['coremedia']'catalina_opts']['cm7-studio-tomcat'] = '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'
