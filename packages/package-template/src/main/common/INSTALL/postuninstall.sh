#!/bin/bash
#Post Uninstallation starts here
set -e
# $1 == 0 --> uninstall
# $1 == 1 --> update
if [ $1 -eq 0 ]; then

  if [ -d @TMP_ROOT@/@APPLICATION_NAME@ ]; then
   rm -rf @TMP_ROOT@/@APPLICATION_NAME@
  fi
fi

#if [ $1 -eq 1 ] ; then
  #do nothing
#fi

