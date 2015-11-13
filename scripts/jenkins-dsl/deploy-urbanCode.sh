# Verbose output and error handling
set -x
set -e


# Settings
tmp_dir=`mktemp -d`
LOCAL_ARCHIVE_DIR=${tmp_dir}/coremediaArchive
DEPLOY_FOLDER=${LOCAL_ARCHIVE_DIR}/opt/deploy
RPM_REPO_ROOT=${LOCAL_ARCHIVE_DIR}/var/tmp
LICENSE_DIR=${LOCAL_ARCHIVE_DIR}/etc/coremedia/licenses


# Compress cookbooks to cookbooks.tar.gz for later use
cd ${WORKSPACE}/boxes/chef/chef-repo/
rm -rf cookbooks*.tar.gz
berks package
mv cookbooks*.tar.gz cookbooks.tar.gz

mkdir -p ${DEPLOY_FOLDER}
mkdir -p ${RPM_REPO_ROOT}
mkdir -p ${LICENSE_DIR}


# Copy scripts to folder
cp -rv "${WORKSPACE}/scripts" ${DEPLOY_FOLDER}

# Copy Chef repo to server
cp -rv "${WORKSPACE}/boxes/chef/chef-repo" ${DEPLOY_FOLDER}

# Copy RPMs to RPM repo
cp -rv "${WORKSPACE}/boxes/target/shared/rpm-repo" ${RPM_REPO_ROOT}

# Download licenses to folder
wget "http://localhost:8081/nexus/service/local/artifact/maven/redirect?r=public&g=com.boots.cms.license&v=LATEST&p=zip&c=cms&a=${LICENSE}" -O "${LICENSE_DIR}/license-cms.zip"
wget "http://localhost:8081/nexus/service/local/artifact/maven/redirect?r=public&g=com.boots.cms.license&v=LATEST&p=zip&c=mls&a=${LICENSE}" -O "${LICENSE_DIR}/license-mls.zip"
wget "http://localhost:8081/nexus/service/local/artifact/maven/redirect?r=public&g=com.boots.cms.license&v=LATEST&p=zip&c=rls&a=${LICENSE}" -O "${LICENSE_DIR}/license-rls.zip"

# build archive
cd ${LOCAL_ARCHIVE_DIR}
tar -zcvf ${tmp_dir}/coremedia-$(date +%Y-%m-%d-"%H-%M-%S").tar.gz *
scp -rv *.tar.gz bootsucd@159.8.163.204:/data/components/coremedia

# Clean up
rm -rf ${tmp_dir}

set +e
set +x