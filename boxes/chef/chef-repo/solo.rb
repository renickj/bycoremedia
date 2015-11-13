currentDir = Dir.pwd
cookbook_path [ currentDir + "/cookbooks" ]
data_bag_path currentDir + "/data_bags"
environment_path currentDir + "/environments"
role_path currentDir + "/roles"
log_level :info
log_location STDOUT
solo true
