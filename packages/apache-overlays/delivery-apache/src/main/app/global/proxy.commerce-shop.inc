ProxyPreserveHost On

Include conf.d/global/proxy.commerce-shop-cae.inc

ProxyPass /wcs balancer://commerce-shop-cluster/wcs timeout=600
ProxyPass /wcsstore balancer://commerce-shop-cluster/wcsstore timeout=600
ProxyPass /webapp balancer://commerce-shop-cluster/webapp timeout=600
ProxyPass / balancer://commerce-shop-cluster/ timeout=600
ProxyPassReverse /wcs balancer://commerce-shop-cluster/wcs
ProxyPassReverse /wcsstore balancer://commerce-shop-cluster/wcsstore
ProxyPassReverse /webapp balancer://commerce-shop-cluster/webapp
ProxyPassReverse / balancer://commerce-shop-cluster/

# ServerAlias shop-tools need /lobtools
ProxyPass /lobtools balancer://commerce-shop-cluster/lobtools timeout=600
ProxyPassReverse /lobtools https://commerce-shop-cluster/lobtools

# ServerAlias shop-orgadmin need /orgadminconsole
ProxyPass /orgadminconsole balancer://commerce-shop-cluster/orgadminconsole timeout=600
ProxyPassReverse /orgadminconsole https://commerce-shop-cluster/orgadminconsole




