#
# Cookbook Name:: coremedia
# Provider:: properties
#
#  This lightweight provider (LWP) provides the action to update java properties file based on the configuration given by
# the node hash below coremedia:configuration
#

action :configure do
  current_props = Hash.new
  notify = false
  configuration_file = "#{node["coremedia"]["configure_root"]}/#{new_resource.application_name}.properties"
  if @new_resource.default_configuration.nil? || @new_resource.default_configuration.empty?
    default_configuration_file = "#{node["coremedia"]["install_root"]}/#{new_resource.application_name}/INSTALL/#{new_resource.application_name}.properties"
  else
    default_configuration_file = @new_resource.default_configuration
  end

  if ::File.exists?(configuration_file)
    current_props = load_properties(configuration_file)
  end

  if ::File.exists?(default_configuration_file)
    default_props = load_properties(default_configuration_file)
  else
    raise("cannot find default properties file at #{default_configuration_file}")
  end

  changed_props = Hash.new
  chef_props = node["coremedia"]["configuration"].attribute?(new_resource.application_name) ?
          node["coremedia"]["configuration"][new_resource.application_name] : node["coremedia"]["configuration"]
  unless chef_props.nil?
    chef_props.each do |k, v|
      if default_props.has_key?(k)
        default_props[k] = v.to_s
      end
    end
  end
  default_props.each do |k, v|
    if !current_props.has_key?(k) || current_props[k] != v
      changed_props[k] = v.to_s
    end
  end

  #if default_props != current_props
  unless changed_props.empty?
    temp_file = Tempfile.new(new_resource.application_name)
    ::File.open(temp_file.path, "w+:iso-8859-1") do |file|
      file.flock(::File::LOCK_EX)
      file.puts("# generated by Chef")
      default_props.keys.sort.each do |key|
        # http://stackoverflow.com/questions/6209480/how-to-replace-backslash-with-double-backslash
        value = default_props[key].to_s.gsub(/\\/, "\\\\\\\\")
        file.puts("#{key.to_s}=#{value}\n")
      end
      file.flock(::File::LOCK_UN)
    end

    diff.diff(configuration_file, temp_file.path)
    diff_desc = 'suppressed sensitive resource'
    unless new_resource.sensitive
      diff_desc = diff.for_output
    end
    converge_by(diff_desc) do
      # we cannot use atomic updates here, copying is sufficient
      Chef::FileContentManagement::Deploy.strategy(false).deploy(temp_file.path, configuration_file)
    end
    notify = true
    unless new_resource.skip_reconfigure
      `#{new_resource.reconfigure_cmd}`
    end
  end
  new_resource.updated_by_last_action(notify)
end


def load_properties(file_path)
  props = Hash.new
  # check each line if it is a comment line or if it is empty. If it does then skip it and go to the next line.
  ::File.open(file_path, "r:iso-8859-1") do |file|
    line_number = 0
    file.read.each_line do |line|
      line.strip!
      line_number += 1
      # Skip empty / comment lines.
      next if (line.length == 0 or line[0] == ?#)
      i = line.index("=")
      if i
        # read key and value.
        name = line[0..i - 1].strip
        value = line[i + 1..-1].strip
        props[name] = value.gsub(/\\\\/, "\\")
      else
        # Not a key-value pair.
        Chef::Log.warn("Invalid property format on line #{line_number} in file #{file_path}.")
      end
    end
  end
  props
end

def diff
  @diff ||= Chef::Util::Diff.new
end
