unless node["coremedia"]["zip"]["archive_url"].empty?
  remote_file "#{Chef::Config[:file_cache_path]}/coremedia-zip-repository.zip" do
    source node["coremedia"]["zip"]["archive_url"]
    backup false
  end

  directory node["coremedia"]["zip"]["dir"] do
    recursive true
  end

  if platform_family?("windows")
    windows_zipfile ::File.dirname(node["coremedia"]["zip"]["dir"]) do
      source "#{Chef::Config[:file_cache_path]}/coremedia-zip-repository.zip"
      overwrite true
    end
  else
    package "unzip" do
      retries node["coremedia"]["package"]["retries"]
    end

    execute "unzip -qq -uo -j #{Chef::Config[:file_cache_path]}/coremedia-zip-repository.zip -d #{node["coremedia"]["zip"]["dir"]}"
  end
end
