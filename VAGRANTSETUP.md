# Quickstart Guide for the Vagrant Chef Setup

## Maven  


You can simply switch between developing against a shared environment and a local virtual environment. You only 
need to activate the `vagrant` profile when you want to connect a Tomcat container in your workspace with a 
service from the virtual environment. No other changes are necessary.   
By activating the `vagrant` profile you aren't changing any artifact, as 
the properties set by this profile, shouldn't be used directly or indirectly by any filtering task in the workspace. Make sure
to keep it that way or you may end up with non-portable builds.

## Vagrant 

The [Vagrantfile](./Vagrantfile) is the main configuration facility to boostrap your virtual environment. Because every
developer should optimize this setup to match his current needs, we integrated the [Nugrant](https://github.com/maoueh/nugrant) Vagrant
plugin to configure specific parts of the VM creation or the Chef configuration.

To use this feature create a `.vagrantuser` file beside the `Vagrantfile`. The `.vagrantfile` is the configuration file
for the Vagrant `nugrant` plugin. It allows you to externalize configuration on project or user level. The syntax is YAML and 
all values of the `config.user.defaults` hash defined in the `Vagrantfile` can be configured externally.

An example of using this facility is to provide your box with more memory and cpu power. To do so add the following snippet to 
your `.vagrantuser` file:

```yaml
vm:
  cpu: "4"
  memory: "8192"
```

There are many more useful plugins for Vagrant. Beside the required plugins, we encourage to use the `vagrant-cachier` plugin,
a plugin that externalizes the caches of any remote file retrieved in your box to your workstations disk and synchronizes these
cache directories with your boxes on startup. You may not be able to achieve offline provisioning with it, but startup will be
reduced a lot.

## Chef

In order to ease the configuration for the development setup, separate roles were introduced for the Vagrant setup. You
find the roles in the [./boxes/chef/vagrant/chef-repo](./boxes/chef/vagrant/chef-repo) folder. Feel free to add custom roles
that match your most efficient and minimalistic development setup.

By default there are six roles defined, `base`, `storage`, `management`, `studio`, `delivery` and `replication`.
With the exception of the `replication` role, the above roles always include their preceding role, i.e.
```role[management]``` includes ```role[storage]```.

### The "base" Role

The base role defines common attributes and its runlist defines some convenience recipes like the yum setup, chef reporting 
and the process dashboard. You should always include this role, when writing a custom role.

### The "storage" Role

The `storage` role sets up all database services, i.e. MySQL and MongoDB including the creation of database schemas.
Use this role, if you want to start the server applications from within your IDE i.e. when you want to debug the Content-Management-Server.
This way, you don't have to ask yourself if you actually have installed the correct version of a storage service. 

### The "management" Role

The `management` role sets up all services, that you need to run the CoreMedia Studio. This includes the 
Content-Management-Server, the Master-Live-Server, the Workflow-Server, the Solr search engine, the Content-Feeder and 
the CAE-Feeder and one Elastic Worker. Use this role, if you want to develop the CoreMedia Studio or the Preview from within
your IDE.

### The "studio" Role

The `studio` role sets up the CoreMedia Studio, the Preview, the SiteManager, the WebDAV integration and the Apache Webserver. 
Use this role to test the whole management stack as a box or to safely prepare a virtual machine for a feature demonstration. 

### The "delivery" Role

The `delivery` role sets up the Delivery and the Apache Webserver. Use this role to safely set up the whole CoreMedia Blueprint
stack or during Continuous Integration jobs in Jenkins. Don't use the role for Studio or CAE development, for this purpose the 
`management` role is more lightweight, provides faster turnarounds and is easier to handle by your workstation. 

### The "replication" Role

The `replication` role sets up a Replication-Live-Server and a Solr slave. The role includes the management and delivery role and
overwrites the node attributes for the delivery CAE to use the replicator and the Solr slave.
  
                                      
### Customize the Runlist

To configure the runlist of chef and set a specific role, edit your `.vagrantuser` file, i.e. :

```yaml
chef:
  run_list:
    - "role[storage]"
    - "role[my-role]"
    - "recipe[my-recipe]"
```
