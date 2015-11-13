coremedia_tool "cm7-css-importer"

# create import folder
["", "/css", "/yaml"].each do |folder|
  directory "#{node["coremedia"]["configuration"]["configure.CSS_IMPORT_INBOX_DIR"]}#{folder}" do
    unless platform_family?("windows")
      owner node["coremedia"]["user"]
      group node["coremedia"]["user"]
    end
  end
end
