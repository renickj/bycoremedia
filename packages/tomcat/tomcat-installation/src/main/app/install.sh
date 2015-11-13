#!/bin/bash
DIR="`dirname \"$0\"`"

echo "Start installing @APPLICATION_NAME@ at @APPLICATION_INSTALL_ROOT@"

$DIR/INSTALL/preinstall.sh 1

if ! test -d @APPLICATION_INSTALL_ROOT@; then
  mkdir -p @APPLICATION_INSTALL_ROOT@
fi

cp -r $DIR/* @APPLICATION_INSTALL_ROOT@/
chown -R @INSTALL_USER@:@INSTALL_GROUP@ @APPLICATION_INSTALL_ROOT@

echo "You can now delete this directory. To uninstall call the uninstall.sh script at @APPLICATION_INSTALL_ROOT@"