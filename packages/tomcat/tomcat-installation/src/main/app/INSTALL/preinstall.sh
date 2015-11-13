#!/bin/sh
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
fi
