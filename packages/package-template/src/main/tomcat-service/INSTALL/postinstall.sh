#!/bin/bash
# $1 == 1 --> initial installation
# $1 == 2 --> upgrade
set -e
if @postconfiguration.enable@ ; then
  @APPLICATION_INSTALL_ROOT@/INSTALL/install-config.sh
  su @INSTALL_USER@ -c '@APPLICATION_INSTALL_ROOT@/INSTALL/reconfigure.sh @APPLICATION_NAME@ @APPLICATION_INSTALL_ROOT@'
fi

if [ $1 -eq 1 ]; then

  #Create logging dir
  mkdir -p @APPLICATION_LOG_DIR@
  chown -R @INSTALL_USER@:@INSTALL_GROUP@ @APPLICATION_LOG_DIR@

  cp @APPLICATION_INSTALL_ROOT@/INSTALL/start-service.sh /etc/init.d/@APPLICATION_NAME@
  cp @APPLICATION_INSTALL_ROOT@/INSTALL/preglow.perl.sh /etc/init.d/preglow.perl.sh
  cp @APPLICATION_INSTALL_ROOT@/INSTALL/preglow-pages.txt /etc/init.d/preglow-pages.txt
  chmod 700 /etc/init.d/@APPLICATION_NAME@
  chmod 600 /etc/init.d/preglow.perl.sh
  chmod 600 /etc/init.d/preglow-pages.txt

  cp @APPLICATION_INSTALL_ROOT@/INSTALL/catalina-config.sh @CONFIGURE_ROOT@/@APPLICATION_NAME@.conf
  chmod 644 @CONFIGURE_ROOT@/@APPLICATION_NAME@.conf

  # Register as service
  chkconfig @APPLICATION_NAME@ on

fi

#if [ $1 -gt 1 ]; then
#do nothing
#fi
