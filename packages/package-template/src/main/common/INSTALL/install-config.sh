#!/bin/bash
set -e
DIR="`dirname \"$0\"`"

if [ ! -f @CONFIGURE_ROOT@/@APPLICATION_NAME@.properties ]; then
  if [ -f $DIR/@APPLICATION_NAME@.properties ]; then
    cp $DIR/@APPLICATION_NAME@.properties @CONFIGURE_ROOT@
  else
    echo "  no default configuration found. To install @APPLICATION_NAME@ properly, you need to"
    echo "  create a properties file at @CONFIGURE_ROOT@/@APPLICATION_NAME@.properties"
  fi
fi