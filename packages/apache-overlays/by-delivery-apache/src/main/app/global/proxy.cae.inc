ProxyPreserveHost On

ProxyPass /blueprint balancer://cae-cluster/blueprint timeout=60
ProxyPass / balancer://cae-cluster/blueprint/servlet/ timeout=60

ProxyPassReverseCookiePath /blueprint /
