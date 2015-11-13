# Verbose output and error handling
set -x
set -e

# Job parameters
NODE_HOST=$NODE_HOST
ENVIRONMENT=$ENVIRONMENT

# Settings
DEPLOY_FOLDER=/opt/deploy

# Create target folder on target node
ssh root@${NODE_HOST} "rm -rf /opt/coremedia/data/* && mkdir -p ${DEPLOY_FOLDER}/"

# Copy scripts to server
scp -rv "${WORKSPACE}/scripts" root@${NODE_HOST}:${DEPLOY_FOLDER}

# Copy Chef repo to server
scp -rv "${WORKSPACE}/boxes/chef/chef-repo" root@${NODE_HOST}:${DEPLOY_FOLDER}

# Copy content and users
scp -rv "${WORKSPACE}/boxes/target/shared/content" root@${NODE_HOST}:${DEPLOY_FOLDER}

# Unpack cookbooks
ssh root@${NODE_HOST} "cd ${DEPLOY_FOLDER}/chef-repo && tar xvfz cookbooks*.tar.gz"

# Execute chef-solo
ssh root@${NODE_HOST} "cd ${DEPLOY_FOLDER}/chef-repo && chef-solo --force-formatter --format doc -c solo.rb -E ${ENVIRONMENT} -j nodes/contentImport.json -N ${NODE_NAME}"

# End
set +e
set +x