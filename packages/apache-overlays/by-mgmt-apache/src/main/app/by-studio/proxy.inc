ProxyPass /blueprint balancer://preview-cluster/blueprint timeout=60
ProxyPass /@STUDIO_WEBAPP_NAME@ balancer://studio-cluster/@STUDIO_WEBAPP_NAME@ timeout=60

ProxyPassReverse / ajp://@STUDIO_HOST@:@STUDIO_AJP_PORT@/

