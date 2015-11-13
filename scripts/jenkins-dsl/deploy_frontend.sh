# Verbose output and error handling
set -x
set -e

ENVIRONMENT=$ENVIRONMENT

# Job parameters
CSS_IMPORTER_DIR=${WORKSPACE}/modules/cmd-tools/css-importer-application/target/css-importer
CUSTOMER_THEME_DIR=${WORKSPACE}/modules/extensions/estore/estore-theme

# Copy capclient.properties for DEV
cp ${WORKSPACE}/scripts/config/$ENVIRONMENT/capclient-css-importer.properties ${CSS_IMPORTER_DIR}/properties/corem/capclient.properties

# Run CSS-Importer
cd ${WORKSPACE}/modules/cmd-tools/css-importer-application/target/css-importer
bin/cm css-import

# TODO Bulkpublish /Themes