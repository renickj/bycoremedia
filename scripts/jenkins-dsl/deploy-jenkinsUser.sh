# Verbose output and error handling
set -x
set -e

#read node and environment from Job parameters
NODE_HOST=$NODE_HOST
ENVIRONMENT=$ENVIRONMENT

# Settings
DEPLOY_FOLDER=/opt/deploy
RPM_REPO_ROOT=/var/tmp
CM_CONFIG=/etc/coremedia

# Compress cookbooks to cookbooks.tar.gz for later use
cd ${WORKSPACE}/boxes/chef/chef-repo/
rm -rf cookbooks*.tar.gz
berks package
mv cookbooks*.tar.gz cookbooks.tar.gz

# Create target folder on target node
ssh jenkins@${NODE_HOST} "mkdir -p ${DEPLOY_FOLDER}/chef-repo"

# Copy scripts to server
scp -rv "${WORKSPACE}/scripts" jenkins@${NODE_HOST}:${DEPLOY_FOLDER}

# Copy Chef repo to server
scp -rv "${WORKSPACE}/boxes/chef/chef-repo" jenkins@${NODE_HOST}:${DEPLOY_FOLDER}

# Copy artifacts, if required
if [ ${COPY_ARTIFACTS} == "true" ]; then
    # Clear RPM repo
    ssh jenkins@${NODE_HOST} "rm -rf ${RPM_REPO_ROOT}/rpm-repo/*.rpm"

	# Create target rpm repo
	ssh jenkins@${NODE_HOST} "mkdir -p ${RPM_REPO_ROOT}"

	# Copy RPMs to RPM repo
	scp -rv "${WORKSPACE}/boxes/target/shared/rpm-repo" jenkins@${NODE_HOST}:${RPM_REPO_ROOT}
fi

# Copy licenses to node

# Create tmp dir
tmp_dir=`mktemp -d`
mkdir ${tmp_dir}/licenses

# Download licenses
wget "http://localhost:8081/nexus/service/local/artifact/maven/redirect?r=public&g=com.boots.cms.license&v=LATEST&p=zip&c=cms&a=${LICENSE}" -O "${tmp_dir}/licenses/license-cms.zip"
wget "http://localhost:8081/nexus/service/local/artifact/maven/redirect?r=public&g=com.boots.cms.license&v=LATEST&p=zip&c=mls&a=${LICENSE}" -O "${tmp_dir}/licenses/license-mls.zip"
wget "http://localhost:8081/nexus/service/local/artifact/maven/redirect?r=public&g=com.boots.cms.license&v=LATEST&p=zip&c=rls&a=${LICENSE}" -O "${tmp_dir}/licenses/license-rls.zip"

# Copy licenses to node
scp -rv "${tmp_dir}/licenses" jenkins@${NODE_HOST}:${CM_CONFIG}

# Clean up
rm -rf ${tmp_dir}

# Provision the node
ssh jenkins@${NODE_HOST} "export http_proxy=http://172.18.112.55:80/ && https_proxy=http://172.18.112.55:80/ && ${DEPLOY_FOLDER}/scripts/provision-node-sudo.sh '${DEPLOY_FOLDER}' ${NODE_HOST} ${ENVIRONMENT}"

set +e
set +x