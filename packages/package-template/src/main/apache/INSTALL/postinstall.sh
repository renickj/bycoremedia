#!/bin/bash
# $1 == 1 --> initial installation
# $1 == 2 --> upgrade

if @postconfiguration.enable@ ; then
  /etc/httpd/conf.d/INSTALL/install-config.sh
  su @INSTALL_USER@ -c '/etc/httpd/conf.d/INSTALL/reconfigure.sh @APPLICATION_NAME@ /etc/httpd/conf.d'
fi

if [ $1 -eq 1 ] ; then
 chkconfig httpd on
fi

#if [ $1 -gt 1 ] ; then
#fi

# add ServerName $HOSTNAME to httpd.conf to prevent "Could not reliably determine the server's fully qualified domain name" warning
if ! grep -q 'ServerName \$HOSTNAME' /etc/httpd/conf/httpd.conf ; then
    echo "ServerName \$HOSTNAME" >> /etc/httpd/conf/httpd.conf
fi     