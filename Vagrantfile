Vagrant.require_version ">= 1.7.4"

[
  {:name => "vagrant-omnibus", :version => "= 1.4.1"},
  {:name => "vagrant-ohai", :version => "= 0.1.12"},
  {:name => "nugrant", :version => "= 2.1.2"},
  {:name => "vagrant-berkshelf", :version => "= 4.0.4"}
].each do |plugin|

  if not Vagrant.has_plugin?(plugin[:name], plugin[:version])
    raise "#{plugin[:name]} #{plugin[:version]} is required. Please run: vagrant plugin install #{plugin[:name]} --plugin-version \"#{plugin[:version]}\""
  end
end

Vagrant.configure("2") do |config|
  # Use CentOS 6.7 base box provided via
  #
  #   https://vagrantcloud.com/coremedia/base
  #
  # (created via https://github.com/coremedia-contributions/boxes/tree/base)
  #
  config.vm.box = "coremedia/base"
  config.vm.box_version = "~> 1.16"
  config.vm.post_up_message = IO.read(File.join(File.dirname(__FILE__), 'OVERVIEW.md'))

  # vagrant-cachier plugin
  if Vagrant.has_plugin?('vagrant-cachier')
    # http://fgrehm.viewdocs.io/vagrant-cachier/usage
    config.cache.auto_detect = true
    config.cache.scope = :machine
    config.omnibus.cache_packages = true
  else
    puts 'INFO:  To speed up the provisioning, you should use the vagrant-cachier plugin. Install via:'
    puts '       vagrant plugin install vagrant-cachier --plugin-version ">= 1.2.0"'
  end

  # default config using nugrant plugin, to override these values create a yaml file at one of the following
  # locations (later overrides previous):
  # ~/.vagrantuser
  # ./.vagrantuser
  # i.e.:
  # vm:
  #   cpu: 4
  #   synced_folders:
  #     - host: "/bla"
  #       guest: "/blub"
  # chef:
  #   run_list:
  #     - "role[base]"
  #     - "role[studio]"
  #noinspection RubyStringKeysInHashInspection
  config.user.defaults = {
          "vm" => {
                  "ssh_port" => "2220",
                  "memory" => "4096",
                  "cpu" => "2",
                  "name" => "blueprint-box-betalabs",
                  "name_prefix" => "",
                  "ip" => "192.168.252.100",
                  # use blueprint-box as tld if you want to set aliases in your host file
                  "tld" => "192.168.252.100.xip.io",
                  # add mappings using nugrant
                  'synced_folders' => [{ 'host' => './boxes/target/shared', 'guest' => '/shared' }]
          },
          "proxy" => {
                  "url" => nil,
                  "username" => nil,
                  "password" => nil
          },
          "chef" => {
                  "version" => "12.4.1",
                  "run_list" => %w(role[delivery])
          },
          'yum' => {
                  'mirrors' => {
                          'base' => 'http://mirrorlist.centos.org/?release=$releasever&arch=$basearch&repo=os',
                          'mongodb' => nil,
                          'mysql' => nil
                  }
          },
          "licenses" => {
                  # if you want to define the license somewhere outside the war, i.e. a shared folder
                  "cms" => "properties/corem/license.zip",
                  "mls" => "properties/corem/license.zip",
                  "rls" => "properties/corem/license.zip",
          },
          "wcs" => {
                  "host" => "shop-ref.ecommerce.coremedia.com"
          }
  }
  config.vm.synced_folder "./", "/vagrant", :disabled => true
  config.user.vm.synced_folders.each do |mapping|
    config.vm.synced_folder mapping['host'], mapping['guest']
  end
  config.omnibus.chef_version = config.user.chef.version
  config.ohai.primary_nic = "eth1"
  config.berkshelf.enabled = true
  config.berkshelf.berksfile_path = "./boxes/chef/chef-repo/Berksfile"

  config.vm.define :blueprint do |blueprint_config|
    blueprint_config.vm.hostname = config.user.vm.name
    blueprint_config.vm.network "forwarded_port", :guest => 8999, :host => 8999 #psdash port
    blueprint_config.vm.network "private_network", :ip => config.user.vm.ip

    blueprint_config.vm.provider "virtualbox" do |v|
      v.name = "#{config.user.vm.name_prefix}#{config.user.vm.name}"
      v.memory = config.user.vm.memory
      v.customize ["modifyvm", :id, "--cpus", config.user.vm.cpu]
      v.customize ["storagectl", :id, "--name", "SATA Controller", "--hostiocache", "on"]
    end
    # copy rpms to box so a mvn clean won't delete the repodata, which itself is a chef resource and should not be manipulated from outside
    blueprint_config.vm.provision "shell", :inline => 'mkdir -p /var/tmp/rpm-repo && cp -R -n /shared/rpm-repo/* /var/tmp/rpm-repo'

    blueprint_config.vm.provision "chef_solo" do |chef|
      chef.formatter = "doc"
      unless config.user.proxy.url
        chef.http_proxy = config.user.proxy.url
        chef.https_proxy = config.user.proxy.url
      end
      chef.http_proxy_user = config.user.proxy.username unless config.user.proxy.username.nil?
      chef.http_proxy_pass = config.user.proxy.passsword unless config.user.proxy.password.nil?
      chef.log_level = :info
      chef.verbose_logging = false
      chef.cookbooks_path = "boxes/chef/chef-repo/cookbooks"
      chef.roles_path = "boxes/chef/vagrant-chef-repo/roles"
      chef.run_list = config.user.chef.run_list
      #noinspection RubyStringKeysInHashInspection
      chef.json = {
              # this sets configure.JAVA_HOME for all tomcat services, and java home for the coremedia users shell
              "java" => { "java_home" => "/usr/lib/jvm/java" },
              "yum" => {
                      'main' => {
                              'proxy' => config.user.proxy.url,
                              'proxy_username' => config.user.proxy.username,
                              'proxy_password' => config.user.proxy.password
                      },
                      'base' => {
                              'mirrorlist' => config.user.yum.mirrors.base
                      },
                      'mysql55-community' => {
                              'mirrorlist' => config.user.yum.mirrors.mysql
                      }
              },
              "coremedia" => {
                      "configuration" => {
                              "configure.STUDIO_TLD" => config.user.vm.tld,
                              "configure.WEBDAV_TLD" => config.user.vm.tld,
                              "configure.DELIVERY_TLD" => config.user.vm.tld,
                              "configure.SITE_MANAGER_HOST_NAME" => "editor.#{config.user.vm.tld}",
                              "configure.SITE_MANAGER_WFS_HOST" => "editor.#{config.user.vm.tld}",
                              "configure.SITE_MANAGER_CMS_HOST" => "editor.#{config.user.vm.tld}",
                              "configure.CMS_LICENSE" => config.user.licenses.cms,
                              "configure.MLS_LICENSE" => config.user.licenses.mls,
                              "configure.RLS_LICENSE" => config.user.licenses.rls,
                              "configure.LIVECONTEXT_HOST" => config.user.wcs.host,
                              "configure.LIVECONTEXT_MANAGEMENT_TOOL_WEB_URI" => "https://#{config.user.wcs.host}/lobtools/CoreMediaManagementCenterWrapper.html",
                              "configure.LIVECONTEXT_WCS_URL" => "http://#{config.user.wcs.host}",
                              "configure.LIVECONTEXT_WCS_SECURE_URL" => "https://#{config.user.wcs.host}"
                      }
              },
              'blueprint' => {
                      'yum' => {
                              'mongodb' => {
                                      'mirrorlist' => config.user.yum.mirrors.mongodb
                              }
                      }
              }
      }
    end
  end
end
