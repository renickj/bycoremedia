#!/bin/bash
DIR="`dirname \"$0\"`"


echo "Start installing @APPLICATION_NAME@ at @APPLICATION_INSTALL_ROOT@"

$DIR/INSTALL/preinstall.sh 1

if ! test -d @APPLICATION_INSTALL_ROOT@; then
  mkdir -p @APPLICATION_INSTALL_ROOT@
fi

cp -r $DIR/* @APPLICATION_INSTALL_ROOT@/
chown -R @INSTALL_USER@:@INSTALL_GROUP@ @APPLICATION_INSTALL_ROOT@

if [ ! -f @CONFIGURE_ROOT@/@APPLICATION_NAME@.properties ] && [ -f @APPLICATION_INSTALL_ROOT@/INSTALL/@APPLICATION_NAME@.properties ] && @postconfiguration.enable@; then
 echo "installing default configuration file for @APPLICATION_NAME@ at @CONFIGURE_ROOT@."
 cp $DIR/INSTALL/@APPLICATION_NAME@.properties @CONFIGURE_ROOT@
 chmod 644 @CONFIGURE_ROOT@/@APPLICATION_NAME@.properties
fi

$DIR/INSTALL/postinstall.sh 1

echo "You can now delete this directory. To uninstall call the uninstall.sh script at @APPLICATION_INSTALL_ROOT@"