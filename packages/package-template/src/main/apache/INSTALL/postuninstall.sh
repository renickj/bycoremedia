#!/bin/bash
#Post Uninstallation starts here

# $1 == 0 --> uninstall
# $1 == 1 --> update
if [ $1 -eq 0 ]; then

  if [ -d /etc/httpd/conf.d/INSTALL ]; then
   rm -rf /etc/httpd/conf.d/INSTALL
  fi
fi

#if [ $1 -eq 1 ] ; then
  #do nothing
#fi

