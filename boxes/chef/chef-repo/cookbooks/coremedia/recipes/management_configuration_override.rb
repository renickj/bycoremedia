if Chef::Config[:solo]
  Chef::Log.warn("This recipe uses search. Chef Solo does not support search.")
else
  management_role = node["coremedia"]["role"]["management"]
  management_nodes = search(:node, "role:#{management_role} AND chef_environment:#{node.chef_environment}")
  if management_nodes.length == 0
    raise("No node with role #{management_role} found. Aborting.")
  elsif management_nodes.length > 1
    raise("More than one node with role #{management_role} found. Aborting")
  end
  management_node = management_nodes.first

  node.override["coremedia"]["configuration"]["configure.CMS_HOST"] = management_node["fqdn"]
  node.override["coremedia"]["configuration"]["configure.CMS_IP"] = management_node["ipaddress"]

  node.override["coremedia"]["configuration"]["configure.MLS_HOST"] = management_node["fqdn"]
  node.override["coremedia"]["configuration"]["configure.MLS_IP"] = management_node["fqdn"]

  node.override["coremedia"]["configuration"]["configure.SOLR_MASTER_HOST"] = management_node["fqdn"]

  mongo_nodes = search(:node, "mongodb_cluster_name:#{node["mongodb"]["cluster_name"]} AND recipes:mongodb\\:\\:replicaset AND chef_environment:#{node.chef_environment}")
  if mongo_nodes.length == 0
    mongo_nodes = search(:node, "recipes:mongodb AND chef_environment:#{node.chef_environment}")
    if mongo_nodes.length == 0
      raise("No MongoDB replicaset with name '#{node["mongodb"]["cluster_name"]}' and no standalone MongoDB server found. Aborting.")
    elsif mongo_nodes.length > 1
      raise("No MongoDB replicaset with name '#{node["mongodb"]["cluster_name"]}' found, but multiple nodes running a MongoDB server. Aborting.")
    end
  end
  mongo_addresses = mongo_nodes.collect { |node| "#{node["fqdn"]}:#{node["mongodb"]["port"]}" }
  node.override["coremedia"]["configuration"]["configure.MONGO_ADDRESSES"] = mongo_addresses.join(",")

  node.save
end
