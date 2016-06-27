ProxyPass /blueprint balancer://preview-cluster/blueprint timeout=60
ProxyPass /@STUDIO_WEBAPP_NAME@ balancer://studio-cluster/@STUDIO_WEBAPP_NAME@ timeout=60

ProxyPassReverse / http://@STUDIO_HOST@:@STUDIO_HTTP_PORT@/
ProxyPassReverse / https://@STUDIO_HOST@:@STUDIO_HTTP_PORT@/
