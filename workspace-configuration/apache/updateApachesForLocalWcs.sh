#!/bin/bash
cd `dirname $0`
cd ../..
mvn clean install -pl :delivery-apache -T2C -DskipTests -am -PlocalEnvironment
mvn clean install -pl :studio-apache -T2C -DskipTests -am -PlocalPreviewEnvironment
cd boxes
mvn antrun:run
cd ..
vagrant provision
