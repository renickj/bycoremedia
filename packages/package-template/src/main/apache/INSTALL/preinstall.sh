#!/bin/bash

# $1 == 1 --> initial installation
# $1 == 2 --> upgrade

if [ $1 -eq 1 ] ; then
  # initial installation
  #Create configure dir
  if test -d @CONFIGURE_ROOT@; then
    chown -R root:root @CONFIGURE_ROOT@
  else
    mkdir -p @CONFIGURE_ROOT@
  fi
fi

#if [ $1 -gt 1 ] ; then
# upgrade installation
#fi

