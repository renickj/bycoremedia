#!/bin/bash

set -e

# global check
if @postconfiguration.enable@ ; then
  which java || (logger -p syslog.err -t coremedia/rpm -s "No java on path $PATH, cannot run reconfigure without java"; exit 1)
fi

# $1 == 1 --> initial installation
# $1 == 2 --> upgrade

if [ $1 -eq 1 ] ; then
  # initial installation

  # configure environment
  if test -d @INSTALL_ROOT@; then
    find @INSTALL_ROOT@ -xdev | xargs chown @INSTALL_USER@:@INSTALL_GROUP@
  else
     mkdir -p @INSTALL_ROOT@
  fi

  # Add the "@INSTALL_USER@" user, on Solaris you should replace /usr/sbin/nologin with /usr/bin/false
  /usr/sbin/useradd -c "system user to run coremedia services and applications" -s /bin/bash -m -d @INSTALL_ROOT@ -r @INSTALL_USER@ 2> /dev/null || :

  #Create configure dir
  mkdir -p @CONFIGURE_ROOT@
  chown -R root:root @CONFIGURE_ROOT@

  #Create logging dir
  mkdir -p @LOG_ROOT@
  chown -R @INSTALL_USER@:@INSTALL_GROUP@ @LOG_ROOT@

  #Create PID dir
  mkdir -p @PID_ROOT@
  chown -R @INSTALL_USER@:@INSTALL_GROUP@ @PID_ROOT@

  #Create data dir
  mkdir -p @DATA_ROOT@
  chown -R @INSTALL_USER@:@INSTALL_GROUP@ @DATA_ROOT@

  #Create tmp dir
  mkdir -p @TMP_ROOT@
  chown -R @INSTALL_USER@:@INSTALL_GROUP@ @TMP_ROOT@
fi

#if [ $1 -gt 1 ] ; then
# upgrade installation
#fi

