#!/bin/bash
#
# This script is provisioning the current node

# Debug and error handling
set -x
set -e

# Check input parameters
if [ $# -lt 3 ]
  then
    echo "Usage: provision-node.sh <DEPLOY_FOLDER> <NODE_NAME> <ENVIRONMENT>"
    exit 1;
fi

# Settings
DEPLOY_FOLDER=$1
NODE_NAME=$2
ENVIRONMENT=$3

CURRENT_PATH=`pwd`

# Unpack cookbooks
cd "${DEPLOY_FOLDER}/chef-repo"
tar xvfz cookbooks*.tar.gz

# Execute chef-solo
chef-solo --force-formatter --format doc -c solo.rb -E ${ENVIRONMENT} -j nodes/${ENVIRONMENT}/${NODE_NAME}.json -N ${NODE_NAME}

# End
cd "${CURRENT_PATH}"

set +e
set +x