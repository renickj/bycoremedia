if Chef::Config[:solo]
  Chef::Log.warn("This recipe uses search. Chef Solo does not support search.")
else
  replication_role = node["coremedia"]["role"]["replication"]
  replication_nodes = search(:node, "ec2_placement_availability_zone:#{node["ec2"]["placement_availability_zone"]} AND role:#{replication_role} AND chef_environment:#{node.chef_environment}")
  if replication_nodes.length == 0
    replication_nodes = search(:node, "role:#{replication_role} AND chef_environment:#{node.chef_environment}")
  end
  if replication_nodes.length == 0
    raise("No nodes with role #{replication_role} found. Aborting.")
  end

  delivery_role = node["coremedia"]["role"]["delivery"]
  delivery_nodes = search(:node, "role:#{delivery_role} AND chef_environment:#{node.chef_environment}")
  replication_nodes.each do |rn|
    rn.override["delivery_node_count"] = delivery_nodes.count { |dn| dn["coremedia"]["configuration"]["configure.RLS_HOST"] == rn["fqdn"] }
  end
  replication_node = replication_nodes.min_by { |n| n["delivery_node_count"] }

  node.override["coremedia"]["configuration"]["configure.RLS_HOST"] = replication_node["fqdn"]
  node.override["coremedia"]["configuration"]["configure.RLS_IP"] = replication_node["ipaddress"]
  node.override["coremedia"]["configuration"]["configure.DELIVERY_REPOSITORY_HOST"] = replication_node["fqdn"]
  node.override["coremedia"]["configuration"]["configure.DELIVERY_SOLR_HOST"] = replication_node["fqdn"]

  node.save
end
