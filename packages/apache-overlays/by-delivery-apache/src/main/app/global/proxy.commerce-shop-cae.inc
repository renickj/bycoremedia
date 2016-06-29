# This is for the commerce-led approach only. PerfectChef should not need these rules!
# TODO: clearify if we really need these!

ProxyPreserveHost On

ProxyPass /blueprint balancer://cae-cluster/blueprint timeout=60

ProxyPass /dynamic balancer://cae-cluster/blueprint/servlet/dynamic timeout=60
ProxyPass /service balancer://cae-cluster/blueprint/servlet/service timeout=60
ProxyPassReverse /dynamic balancer://cae-cluster/blueprint/servlet/dynamic
ProxyPassReverse /service balancer://cae-cluster/blueprint/servlet/service

ProxyPassReverseCookiePath /blueprint /




