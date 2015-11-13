#!/bin/bash
# $1 == 1 --> initial installation
# $1 == 2 --> upgrade

set -e

if @postconfiguration.enable@ ; then
  @APPLICATION_INSTALL_ROOT@/INSTALL/install-config.sh
  su @INSTALL_USER@ -c '@APPLICATION_INSTALL_ROOT@/INSTALL/reconfigure.sh @APPLICATION_NAME@ @APPLICATION_INSTALL_ROOT@'
fi

#if [ $1 -eq 1 ]; then
#
#fi

#if [ $1 -gt 1 ]; then
#fi

