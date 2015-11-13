#!/bin/bash
DIR="`dirname \"$0\"`"


echo "Start installing @APPLICATION_NAME@ at /etc/httpd/conf.d"

$DIR/INSTALL/preinstall.sh 1

if ! test -d /etc/httpd/conf.d; then
  mkdir -p /etc/httpd/conf.d
fi

cp -r $DIR/* /etc/httpd/conf.d/
find $DIR -type f | sed 's#^\.#/etc/httpd/conf.d#g' > /etc/httpd/conf.d/installed-files
chown -R @INSTALL_USER@:@INSTALL_GROUP@ /etc/httpd/conf.d

if [ ! -f @CONFIGURE_ROOT@/@APPLICATION_NAME@.properties ] && [ -f /etc/httpd/conf.d/INSTALL/@APPLICATION_NAME@.properties ]; then
 echo "installing default configuration file for @APPLICATION_NAME@ at @CONFIGURE_ROOT@."
 cp $DIR/INSTALL/@APPLICATION_NAME@.properties @CONFIGURE_ROOT@
 chmod 644 @CONFIGURE_ROOT@/@APPLICATION_NAME@.properties
fi

$DIR/INSTALL/postinstall.sh 1

echo "You can now delete this directory. To uninstall call the uninstall.sh script at /etc/httpd/conf.d"