#!/bin/bash
set -e
# $1 == 0 --> uninstall
# $1 == 1 --> update

if [ $1 -eq 0 ]; then

  if [ -f /etc/init.d/@APPLICATION_NAME@ ]; then
    # stopping the service
    service @APPLICATION_NAME@ stop &>/dev/null || :
    chkconfig --del @APPLICATION_NAME@ || echo "no service found"
    rm /etc/init.d/@APPLICATION_NAME@
  fi
fi

#if [ $1 -eq 1 ]; then
# do nothing
#fi
