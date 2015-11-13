# see http://wiki.opscode.com/display/chef/Knife#Knife-ConfiguringYourSystemForKnife for details

node_name ENV['OPSCODE_USER']
client_key ENV['OPSCODE_CLIENT_KEY']
validation_client_name "#{ENV['OPSCODE_ORGNAME']}-validator"
validation_key ENV['OPSCODE_VALIDATION_KEY']
chef_server_url "https://api.opscode.com/organizations/#{ENV['OPSCODE_ORGNAME']}"

cookbook_path %W(#{File.dirname(__FILE__)}/../cookbooks)

encrypted_data_bag_secret ENV['ENCRYPTED_DATA_BAG_SECRET']

log_level :debug
log_location STDOUT
verbose_logging false

# knife-ec2 configuration
# see https://github.com/opscode/knife-ec2 for details

knife[:aws_access_key_id] = ENV['AWS_ACCESS_KEY']
knife[:aws_secret_access_key] = ENV['AWS_SECRET_KEY']
knife[:aws_ssh_key_id] = ENV['EC2_KEYPAIR']
knife[:region] = ENV['EC2_REGION']

knife[:ssh_user] = ENV['EC2_USER']
knife[:identity_file] = ENV['AWS_IDENTITY_FILE']
