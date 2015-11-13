unless node["coremedia"]["configuration"]["configure.STUDIO_APACHE_LOG_DIR"].nil?
  directory node["coremedia"]["configuration"]["configure.STUDIO_APACHE_LOG_DIR"] do
    recursive true
  end
end

unless node["coremedia"]["configuration"]["configure.WEBDAV_APACHE_LOG_DIR"].nil?
  directory node["coremedia"]["configuration"]["configure.WEBDAV_APACHE_LOG_DIR"] do
    recursive true
  end
end

coremedia_apache "cm7-studio-apache"
