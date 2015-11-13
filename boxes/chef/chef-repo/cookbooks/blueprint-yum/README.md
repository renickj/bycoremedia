# blueprint-yum-cookbook

This cookbook is a wrapper cookbook for the yum cookbook to setup your yum repos. Please feel free to customize this repo.

As a start, you can modify the includes of the default recipe to match your needs. I.e. the local recipe is designed 
for development purposes. If you have a repository manager like nexus you should rather use the remote recipe and configure
a remote yum repository.

## attributes

Here, only the attributes that differ from the ones provided by the yum repository resource are listed.

* ```['blueprint']['yum']['local']['path']``` - The path to the local yum repository.
       
* ```['blueprint']['yum']['local']['archive']``` - The URL of the yum repository archive to download.
       
* ```['blueprint']['yum']['remote']['managed']``` - Toggle to switch off the generic remote repository.
       
* ```['blueprint']['yum']['mongodb']['managed']``` - Toggle to switch off the mongodb repository.

* ```['blueprint']['yum']['mysql-community']['managed']``` - Toggle to switch off the mysql repository.

## recipes

* default - configures the yum repos for a machine, this recipe should only include recipes below.

* centos - configures yum repos for centos. This is a wrapper recipe for the official recipe from ```yum-centos``` cookbook.

* mongodb - configures yum repos to install mongodb. All default attributes from the ```repository``` LWRP of the 
yum cookbook can be applied. The attributes are structured below ```['blueprint']['yum']['mongodb']```.

* mysql-community - configures the mysql-community repos to install mysql 5.5. This is a wrapper recipe for the 
official recipe from ```yum-mysql-community``` cookbook. 

* remote - configures a remote repository for blueprint RPMs, i.e. a nexus or artifactory repo.

* local - configures a local repository, i.e. for development purposes with vagrant.

## dependencies

* yum

* yum-centos

* yum-mysql-community


## Tips for development with Vagrant

In order to improve the provisioning speed and to achieve offline provisioning, you should use the excellent
 ```vagrant-cachier``` plugin. With it, you can cache almost any remote file being downloaded on the vagrant box.
 I.e. the chef omnibus installer but also rpm packages. To enable it you need to:
 
1. Install the plugin ```vagrant plugin install vagrant-cachier```
 
2. Enable it, preferably in the super ```Vagrantfile``` in your vagrant home, i.e. ```~/.vagrant.d```. Simply add the
  following if statement within the ```config``` block or add the complete file if you don't yet have such a file.

```ruby
Vagrant.configure("2") do |config|
  if Vagrant.has_plugin?("vagrant-cachier")
    config.cache.auto_detect = true
    config.cache.scope = :machine
    config.omnibus.cache_packages = true
  end
end
```   
