#!/bin/bash
[ "$#" -eq 2 ] || (echo "2 arguments required (application-name, filtering-root, $# provided";exit 1)
set -u


DIR="`dirname \"$0\"`"
APPLICATION_NAME=$1
FILTERING_ROOT=$2
test -d $FILTERING_ROOT || (echo "no application-installation-root found at $FILTERING_ROOT"; exit 1)
test -f $DIR/$APPLICATION_NAME.properties || (echo "no default configuration template found at $DIR/$APPLICATION_NAME.properties";exit 1)
test -f @CONFIGURE_ROOT@/$APPLICATION_NAME.properties || (echo "no application configuration file found at @CONFIGURE_ROOT@/$APPLICATION_NAME.properties, copy $DIR/$APPLICATION_NAME.properties to @CONFIGURE_ROOT@ and configure it ";exit 1)

REQUIRED_TOKENS=`sed '/^\#/d' $DIR/$APPLICATION_NAME.properties  | grep '.*=.*' | sed 's/=.*//g'`

#Check if all configuration tokens are set
missing=""
for token in $REQUIRED_TOKENS; do
 count=`cat @CONFIGURE_ROOT@/$APPLICATION_NAME.properties | grep $token | wc -l`
 if [ $count == 0 ]; then
    missing="$missing $token"
 fi
done

if [ ! -z "$missing" ]; then
  logger -p syslog.err -s -t coremedia/rpm "There are missing tokens in the configuration file @CONFIGURE_ROOT@/$APPLICATION_NAME.properties"
  for t in $missing ; do
    logger -p syslog.err -s -t coremedia/rpm  "$t is missing"
  done
  exit 1
fi

FILTERING_TMP_DIR=$(mktemp -d --tmpdir=$DIR ${APPLICATION_NAME}-templates.XXXX)
if [ -d $DIR/${APPLICATION_NAME}-templates ] ; then
  cp -r $DIR/${APPLICATION_NAME}-templates/* $FILTERING_TMP_DIR/
fi
java -jar $FILTERING_ROOT/INSTALL/token-replacer-onejar.jar -f @CONFIGURE_ROOT@/$APPLICATION_NAME.properties -d $FILTERING_TMP_DIR
CHECK=$?
if test "$CHECK" == "0"; then
  cp -r $FILTERING_TMP_DIR/* $FILTERING_ROOT/
  rm -rf $FILTERING_TMP_DIR
else
  cp $FILTERING_ROOT/INSTALL/token-replacer-onejar.jar $FILTERING_TMP_DIR/
  logger -p syslog.err -t coremedia/rpm -s "Error while replacing tokens in the configuration files.
  RPM installation has been aborted but you can analyze the filtering process by executing:
  su @INSTALL_USER@ -c \"java -jar $FILTERING_TMP_DIR/token-replacer-onejar.jar -f @CONFIGURE_ROOT@/$APPLICATION_NAME.properties -d $FILTERING_TMP_DIR  -v \"
  To recover you can then filter the templates below $FILTERING_TMP_DIR manually, remove $FILTERING_TMP_DIR/token-replacer-onejar.jar
  and copy the content of $FILTERING_TMP_DIR to $FILTERING_ROOT."
  exit 1
fi
