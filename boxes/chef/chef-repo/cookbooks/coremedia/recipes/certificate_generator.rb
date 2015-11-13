webdav_host = node["coremedia"]["configuration"]["configure.WEBDAV_TLD"]
script_file = "/opt/coremedia/certificate-generator.sh"
private_key = "/etc/pki/tls/private/localhost.key"
certificate_file = "/etc/pki/tls/certs/localhost.crt"
credentials = %W(-- SomeState SomeCity SomeOrganization SomeOrganizationUnit *.#{webdav_host} root@#{webdav_host})


#assemble certificate generator script

log("Creating SSL certificate")
template script_file do
  source "certificate_generator.erb"
  variables({
                    :credentials => credentials,
                    :private_key => private_key,
                    :certificate => certificate_file
            })
end

execute "create_cert_generator" do
  command "sh #{script_file}"
  notifies :restart, "service[httpd]", :delayed
end
