unless node["coremedia"]["configuration"]["configure.DELIVERY_APACHE_LOG_DIR"].nil?
  directory node["coremedia"]["configuration"]["configure.DELIVERY_APACHE_LOG_DIR"] do
    recursive true
  end
end

coremedia_apache "cm7-delivery-apache"
