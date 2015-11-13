cd %~dp0
cd ../..
call mvn clean install -pl :delivery-apache -T2C -DskipTests -am -PlocalEnvironment
call mvn clean install -pl :studio-apache -T2C -DskipTests -am -PlocalPreviewEnvironment
cd boxes
call mvn antrun:run
cd ..
vagrant provision
