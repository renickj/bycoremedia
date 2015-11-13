# Description

Installs and configures CoreMedia RPM based services and tools on Red Hat-like Systems. Includes LWRP for:

* importing and publishing content

* upload workflows

* probe services using the CoreMedia Probedog Tool

# Requirements

Red Hat Enterprise Linux 6 distributions within this platform family.

# Attributes

* `coremedia['version']['global']`
  - The global version to install. If set to `nil` (default), the latest available version will be installed.
   In addition to `global` you can set a specific version to a specific package, eg. `coremedia['version']['cm7-cms-tomcat']`.

* `coremedia['user']`
  - The user to execute CoreMedia tools. This user should be the same as the one the CoreMedia services are started with.
    The attribute must be identical to the `INSTALL_USER` maven property.

* `coremedia['install_root']`
  - The directory where all CoreMedia applications are installed below. Defaults to `/opt/coremedia`. This attribute must be
  identical to the `INSTALL_ROOT` maven property.

* `coremedia['configure_root']`
  - The directory where all CoreMedia deployment configuration files are located. Defaults to `/etc/coremedia`. this attribute must be
   identical to the `CONFIGURE_ROOT` maven property.

* `coremedia['content_archive']`
  - The local path of the content archive containing content to import, publish and optionally a users file to restore users. Defaults to `/shared/content/content-user.zip`.

* `coremedia['db']['host']`
  - The host for which database schemas should be generated for.

* `coremedia['db']['schemas']`
  - An array of database schemas to create on a node

* `coremedia['workflows']['builtin']`
  - An array of filenames of the builtin workflows to upload.
    Defaults to `studio-simple-publication.xml`, `immediate-publication.xml`, `studio-two-step-publication.xml`, `three-step-publication.xml`, `global-search-replace.xml`

* `coremedia['workflows']['custom']`
  - Below this hashkey, you can define custom workflow definitions and corresponding code and resources jars to upload. E.g. :
    ``` ruby
    'coremedia' => { 'workflows' => { 'custom' => { 'my-workflow' => {
                                    'definition' => '/opt/coremedia/my-workflow.xml',
                                    'jar' =>/opt/coremedia/my-workflow.jar'
                                    }}}}
    ```
    
* `coremedia['tomcat']['manager']['credentials']`
  - A hash of credentials for the tomcat manager application. If you want the memory report to be written,
    you need at least one credential with the manager-script role. E.g.:
    ``` ruby
   'coremedia' => { 'tomcat' => { 'manager' =>{ 'credentials' => {
                       'admin' => {
                               'username' => 'admin',
                               'password' => 'tomcat',
                               'roles'    => 'manager-gui'
                       },
                       'script' => {
                               'username' => 'script',
                               'password' => 'tomcat',
                               'roles'    => 'manager-jmx,manager-script'
                       }
                  }}}
    ```

# Recipes

## default

The default recipe does nothing.

## chef_logging

Configures Chef logging, eg. disables verbose logging.

## db_schemas

Creates database schemas for all values of the array defined in attribute `coremedia['db']['schemas']`.

## content_management_server

Installs and configures a CoreMedia Content Management Server as a service. If content is present
in a list of local archives configured with `coremedia['content_archive']`, the content will be imported and published.

## master_live_server

Installs and configures a CoreMedia Master Live Server as a service.

## replication_live_server

Installs and configures a CoreMedia Replication Live Server as a service.

## workflow_server

Installs and configures a CoreMedia Workflow Server as a service. With `coremedia['workflows']['builtin']` you can define the array of built-in
workflows that should be uploaded to the workflow server. Within the hash `coremedia['workflows']['custom']` you can define workflows and optional
corresponding jars to upload.

## studio

Installs and configures the CoreMedia Studio, Sitemanager, WebDAV and the Preview as a service.

## studio_apache

Installs and configures the Apache rewrite rules for the service installed by the `studio` recipe.

## delivery

Installs and configures the CoreMedia CAE for delivery as a service.

## delivery_apache

Installs and configures the Apache rewrite rules for the service installed by the `delivery` recipe.

## caefeeder_preview

Installs and configures the CoreMedia CAE Feeder for the management content as a service.

## caefeeder_live

Installs and configures the CoreMedia CAE Feeder for the live content as a service.

## solr_master

Installs the Solr Search engine master as a service.

## solr_slave

Installs the Solr Search engine slave as a service.

## reporting

Installs Chef report handlers. By default it installs:
- an update packages handler, that generates a report about all updated packages.
- a memory consumption report, that generates a report about the jvm memory consumptions of coremedia services.
- a profiling report, that generates a report about the time being consumed by cookbook, recipe and resources.

## management_configuration_override

A recipe to use with chef server. This recipe uses the chef server search functionality to set node properties for the management and studio boxes.

## replication_configuration_override

A recipe to use with chef server. This recipe uses the chef server search functionality to set node properties for the replication and delivery boxes.

# Resources/Providers

## probedog

This LWRP runs a CoreMedia Probedog. The resource can be used to check the availability of CoreMedia services. There are probedog
resources defined by default in the recipes `content_management_server`, `master_live_server`, `replication_live_server`, `workflow_server`,
`studio`, `delivery`, `caefeeder_live` and `caefeeder_preview`. This resource requires a Probedog tool to be present.

### Actions

 - :check: runs a probedog with a timeout until the service is available.

#### Attribute Parameters

- probe: name of the Probe.
- tool_name: name of the tool containing the Probedog.Set to resource name if not defined explicitly.
- timeout: the timeout when to stop probing.

#### Example

``` ruby
coremedia_probedog "cm7-cms-tomcat" do
  probe "ProbeContentServer"
  timeout "300"
end
```

## configuration

This LWRP updates the existing/default configuration properties with the key value pairs provided by the hash of the Chef
attribute `coremedia[`configuration']`.

### Actions

- :configure: updates the deployment configuration of the CoreMedia service or tool.

#### Attribute Parameters

- application_name: the name of the configuration file. Set to resource name if not defined explicitly.
- skip_reconfigure: whether to skip reconfiguring the application. Defaults to false.
- reconfigure_cmd: the command to reconfigure the application. This attribute is required even if `skip_reconfigure` is set to false.
- default_configuration: the path of the default configuration properties file.
  Defaults to `coremedia['install_root']/<application_name>/INSTALL/<application_name>.properties`.
- sensitive: if set to true, the diff between old and new configuration won't be logged.

#### Example

``` ruby
coremedia_configuration "cm7-cms-tomcat" do
 application_name "cm7-cms-tomcat"
end
```

## content

This LWRP represents a zipped content and users set, that can be imported and published and in case of users restored.
To prevent reimporting of unchanged content, the archives is copied to a working directory and in future runs, the resource
compares the checksum of the copy with the archive. If it has changed it will be extracted and depending resources will be
notified. This can be used to let the import subscribes to an unpack action.

### Actions

- :unpack: extracts the archive if it has changed
- :import: imports the content if it is newer than the last import.
- :bulkpublish: bulkpublishes the content.

#### Attribute Parameters

- archive: the filename of the content zip.
- cms_tools: the path of the tools dir, where the `serverimport.jpif`, the `bulkpublish.jpif` and the `restorusers.jpif` are
  located. Defaults to `/opt/coremedia/cm7-cms-tools`
- working_dir: the path of the directory, to which the zip should be extracted to.
- content_folder: the relative path from the `working_dir` to the folder containing the content.
- users_folder: the relative path from the `working_dir` to the folder containing the users files.
- username: the repository user to import the content with. Defaults to "admin".
- password: the password of the repository user to import the content with. Defaults to "admin".
- timeout: the time in seconds to probe the content management servers availability in advance of importing content.

#### Example

```ruby
coremedia_content "content_users" do
  actions [:unpack,:import,:bulkpublish]
  archive "content-users.zip"
  cms_tools "/opt/coremedia/cm7-cms-tools"
  working_dir "/opt/coremedia/data"
  content_folder "content"
  users_file "users/user.xml"
  username "admin"
  password "admin"
  timeout "400"
end
```

## workflows

This LWRP represents workflows to upload to the CoreMedia Workflow Server. The resource wraps the `upload.jpif` tool and
allows you to schedule the workflow upload whenever the Workflow Servers package, containing the workflows, gets updated.

### Actions

- :upload: uploads either a list of built-in Workflows or a workflow definition file and optionally a jar with extra resources and
  action classes for the workflow.

#### Attribute Parameters

- username: the repository user to upload the workflow with. Defaults to "admin".
- password: the password of the repository user to upload the workflow with. Defaults to "admin".
- wfs-tools: the path of the tools dir, where the `upload.jpif` is located. Defaults to `/opt/coremedia/cm7-wfs-tools`
- builtin_workflows: an array of built-in definition filenames. Defaults to an empty array.
- definition: the path to the definition file to upload.
- jar: the path to the jar to upload together with the definition.
- timeout: the timeout of the workflowserver availability check in advance. Defaults to 5 minutes.

#### Example

```ruby
coremedia_workflows "my_workflows" do
  actions :upload
  username "admin"
  password "admin"
  wfs_tools "/opt/coremedia/cm7-wfs-tools"
  builtin_workflows %w(studio-simple-publication.xml immediate-publication.xml)
  definition "/opt/coremedia/my-workflow.xml"
  jar "/opt/coremedia/my-workflow.jar"
  timeout "400"
end
```





