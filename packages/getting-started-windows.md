#Getting Started On Windows Server

This sketch shows how to get started with CoreMedia 7.5 on a Windows Server 2008 R2 machine. The default setup uses MySQL.
At the bottom of this document you can find an alternative setup for SQL Server.


##Preparation

Apply the following patch to prepare the workspace for a Windows deployment:

    diff --git a/packages/apache-overlays/pom.xml b/packages/apache-overlays/pom.xml
    index b2e2a42..322023d 100644
    --- a/packages/apache-overlays/pom.xml
    +++ b/packages/apache-overlays/pom.xml
    @@ -14,7 +14,7 @@
    
       <properties>
         <filters.dir>${project.basedir}/../../src/main/filters</filters.dir>
    -    <APPLICATION_INSTALL_ROOT>/etc/httpd</APPLICATION_INSTALL_ROOT>
    +    <APPLICATION_INSTALL_ROOT>C:\Apache2.2\conf.d</APPLICATION_INSTALL_ROOT>
       </properties>
         
       <modules>
    diff --git a/packages/pom.xml b/packages/pom.xml
    index 6f411ff..615df8c 100644
    --- a/packages/pom.xml
    +++ b/packages/pom.xml
    @@ -36,13 +36,13 @@
         <!--default properties that cannot be tokenized for installation time filtering-->
         <LOG_ROOT>/var/log/coremedia</LOG_ROOT>
         <PID_ROOT>/var/run/coremedia</PID_ROOT>
    -    <INSTALL_ROOT>/opt/coremedia</INSTALL_ROOT>
    +    <INSTALL_ROOT>C:\CoreMedia</INSTALL_ROOT>
         <TMP_ROOT>/var/cache/coremedia</TMP_ROOT>
         <CONFIGURE_ROOT>/etc/coremedia</CONFIGURE_ROOT>
         <INSTALL_USER>coremedia</INSTALL_USER>
         <INSTALL_GROUP>coremedia</INSTALL_GROUP>
         <APPLICATION_PREFIX>cm7</APPLICATION_PREFIX>
    -    <APPLICATION_INSTALL_ROOT>${INSTALL_ROOT}/${APPLICATION_NAME}</APPLICATION_INSTALL_ROOT>
    +    <APPLICATION_INSTALL_ROOT>${INSTALL_ROOT}\${APPLICATION_NAME}</APPLICATION_INSTALL_ROOT>
         <APPLICATION_NAME>${APPLICATION_PREFIX}-${project.artifactId}</APPLICATION_NAME>
         <APPLICATION_LOG_DIR>${LOG_ROOT}/${APPLICATION_NAME}</APPLICATION_LOG_DIR>
         <APPLICATION_PID>${PID_ROOT}/${APPLICATION_NAME}.pid</APPLICATION_PID>
    @@ -59,8 +59,8 @@

         <!--Apache specific settings-->
    -    <STUDIO_APACHE_CERT_FILE>/etc/pki/tls/certs/localhost.crt</STUDIO_APACHE_CERT_FILE>
    -    <STUDIO_APACHE_CERT_KEYFILE>/etc/pki/tls/private/localhost.key</STUDIO_APACHE_CERT_KEYFILE>
    +    <STUDIO_APACHE_CERT_FILE>C:\Apache2.2\conf\server.crt</STUDIO_APACHE_CERT_FILE>
    +    <STUDIO_APACHE_CERT_KEYFILE>C:\Apache2.2\conf\server.key</STUDIO_APACHE_CERT_KEYFILE>
         <STUDIO_APACHE_REWRITE_LOGLEVEL>0</STUDIO_APACHE_REWRITE_LOGLEVEL>
         <STUDIO_APACHE_LOG_DIR>/var/log/httpd</STUDIO_APACHE_LOG_DIR>
         <DELIVERY_APACHE_REWRITE_LOGLEVEL>0</DELIVERY_APACHE_REWRITE_LOGLEVEL>

Now compile the workspace. Execute the following line in a shell from the root directory of the workspace:

    mvn clean install


##Setup

Start Remote Desktop Connection to connect to the server machine. Make sure to connect the drive containing the
CoreMedia 7 workspace to the remote session because you need to copy some files from the workspace to the remote
machine. Log in as Administrator user.

After logging in on the remote server, start Window Explorer and copy `boxes\target\content-users.zip`,
`boxes\target\zip-repo` and `workspace-configuration\database` from your workspace to
`C:\Users\Administrator\Downloads`.

Start PowerShell and change to the Administrator home directory, if necessary.

    cd C:\Users\Administrator

Import the Background Intelligent Transfer Service (BITS) module to download files during the setup.

    Import-Module BitsTransfer


##7-Zip Installation

Download and install 7-Zip.

    Start-BitsTransfer -Source "http://downloads.sourceforge.net/project/sevenzip/7-Zip/9.20/7z920-x64.msi?r=http%3A%2F%2Fwww.7-zip.org%2F&ts=1349696827&use_mirror=freefr" -Destination ".\Downloads\7z920-x64.msi"
    msiexec.exe /i "C:\Users\Administrator\Downloads\7z920-x64.msi" /qn

Add the 7-Zip installation directory to the `PATH` environment variable.

    [Environment]::SetEnvironmentVariable("PATH", "$env:PATH;$env:ProgramFiles\7-Zip", "Machine")
    $env:PATH="$env:PATH;$env:ProgramFiles\7-Zip"


##FireFox Installation (optional)

Download and install FireFox.

    Start-BitsTransfer -Source "http://download.cdn.mozilla.net/pub/mozilla.org/firefox/releases/10.0.8esr/win32/en-US/Firefox%20Setup%2010.0.8esr.exe" -Destination ".\Downloads\Firefox Setup 10.0.8esr.exe"
    & "Downloads\Firefox Setup 10.0.8esr.exe" -ms


##MySQL Installation

Download and install MySQL.

    Start-BitsTransfer -Source "http://cdn.mysql.com/Downloads/MySQL-5.5/mysql-5.5.28-winx64.msi" -Destination .\Downloads
    msiexec /i "C:\Users\Administrator\Downloads\mysql-5.5.28-winx64.msi" /q

Setup a MySQL instance and use `admin` as root password.

    & 'C:\Program Files\MySQL\MySQL Server 5.5\bin\MySQLInstanceConfig.exe' -i -q "-nMySQL Server 5.5" "-pC:\Program Files\MySQL\MySQL Server 5.5" -v5.5.29 ServiceName=MySQL55 RootPassword=admin

Add the MySQL installation directory to the `PATH` environment variable.

    [Environment]::SetEnvironmentVariable("PATH", "$env:PATH;C:\Program Files\MySQL\MySQL Server 5.5\bin", "Machine")
    $env:PATH="$env:PATH;C:\Program Files\MySQL\MySQL Server 5.5\bin"


##MongoDB Installation

Create directories for logs and database files.

    md C:\var\log\mongodb
    md C:\var\lib\mongodb

Download and extract MongoDB.

    Start-BitsTransfer -Source "http://downloads.mongodb.org/win32/mongodb-win32-x86_64-2008plus-2.2.0.zip" -Destination .\Downloads
    &7z x "-oC:\Program Files" "Downloads\mongodb-win32-x86_64-2008plus-2.2.0.zip"

Install and start the MongoDB service.

    & "C:\Program Files\mongodb-win32-x86_64-2008plus-2.2.0\bin\mongod.exe" --install --logpath C:\var\log\mongodb\mongod.log --dbpath C:\var\lib\mongodb
    Start-Service MongoDB


##Java Runtime Environment Installation

Download and install Java.

    Start-BitsTransfer -Source "http://javadl.sun.com/webapps/download/AutoDL?BundleId=68736" -Destination ".\Downloads\jre-7u7-windows-x64.exe"
    & ".\Downloads\jre-7u7-windows-x64.exe" /s

Create `JAVA_HOME` and `JRE_HOME` environment variables.

    [Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Java\jre7", "Machine")
    [Environment]::SetEnvironmentVariable("JRE_HOME", "C:\Program Files\Java\jre7", "Machine")
    $env:JAVA_HOME="C:\Program Files\Java\jre7"
    $env:JRE_HOME="C:\Program Files\Java\jre7"

##Apache Installation

Download and install Apache.

    Start-BitsTransfer -source "http://mirror.netcologne.de/apache.org/httpd/binaries/win32/httpd-2.2.22-win32-x86-openssl-0.9.8t.msi" -destination Downloads
    msiexec.exe /q /i "Downloads\httpd-2.2.22-win32-x86-openssl-0.9.8t.msi" INSTALLDIR="C:\Apache2.2" SERVERADMIN="test@localhost"

Enable all required modules in the Apache configuration .

    (cat C:\Apache2.2\conf\httpd.conf) | %{$_ -replace "#LoadModule rewrite_module modules/mod_rewrite.so", "LoadModule rewrite_module modules/mod_rewrite.so"} | Set-Content C:\Apache2.2\conf\httpd.conf
    (cat C:\Apache2.2\conf\httpd.conf) | %{$_ -replace "#LoadModule ssl_module modules/mod_ssl.so", "LoadModule ssl_module modules/mod_ssl.so"} | Set-Content C:\Apache2.2\conf\httpd.conf
    (cat C:\Apache2.2\conf\httpd.conf) | %{$_ -replace "#Include conf/extra/httpd-ssl.conf", "Include conf/extra/httpd-ssl.conf"} | Set-Content C:\Apache2.2\conf\httpd.conf
    (cat C:\Apache2.2\conf\httpd.conf) | %{$_ -replace "#LoadModule proxy_module modules/mod_proxy.so", "LoadModule proxy_module modules/mod_proxy.so"} | Set-Content C:\Apache2.2\conf\httpd.conf
    (cat C:\Apache2.2\conf\httpd.conf) | %{$_ -replace "#LoadModule proxy_balancer_module modules/mod_proxy_balancer.so", "LoadModule proxy_balancer_module modules/mod_proxy_balancer.so"} | Set-Content C:\Apache2.2\conf\httpd.conf
    (cat C:\Apache2.2\conf\httpd.conf) | %{$_ -replace "#LoadModule proxy_ajp_module modules/mod_proxy_ajp.so", "LoadModule proxy_ajp_module modules/mod_proxy_ajp.so"} | Set-Content C:\Apache2.2\conf\httpd.conf

Create and install a certificate and a key for SSL communication.

    C:\Apache2.2\bin\openssl.exe req -new -x509 -nodes -out server.crt -keyout server.key -config C:\Apache2.2\conf\openssl.cnf -batch
    mv .\server.crt, .\server.key C:\Apache2.2\conf

Install and start the Apache service.

    C:\Apache2.2\bin\httpd.exe -k install -n "Apache2.2"
    Start-Service Apache2.2


##CoreMedia 7.5 Installation

Create the temporary installation directory for extracting the ZIP archives and the final installation directory.

    md C:\install
    md C:\CoreMedia

Create the database schemas and users. The MySQL password is `admin`.

    .\Downloads\database\mysql\createDB.bat

Extract all ZIP archives to the temporary installation directory.

    $files=Get-ChildItem C:\Users\Administrator\Downloads\zip-repo
    foreach ($file in $files) { & 7z x -y -oC:\install\$($file.BaseName) $file.FullName }
 
Change the repository URL for the Delivery Tomcat to point to the Master Live Server because we are not going to use a Replication Live Server in this setup.

    (gc C:\install\cm7-delivery-tomcat\INSTALL\cm7-delivery-tomcat.properties) | %{$_ -replace "48080", "42080"} | sc C:\install\cm7-delivery-tomcat\INSTALL\cm7-delivery-tomcat.properties

Copy the Tomcat base installation to the final installation directory.

    cp -r C:\install\cm7-tomcat-installation C:\CoreMedia\cm7-tomcat-installation

Run the Tomcat installation scripts. These scripts copy the Tomcat installations to the final installation directory. The Tomcat scripts expect an JRE installation in %JAVA_HOME%\jre. Since we are installed a JRE, the Tomcat scripts will fail, so we temporarily unset `JAVA_HOME`. The Tomcat scripts will then use the `JRE_HOME` environment variable to find the JRE installation.

    cp Env:\JAVA_HOME Env:\OLD_JAVA_HOME
    rm Env:\JAVA_HOME
    C:\install\cm7-solr-master-tomcat\install.bat
    C:\install\cm7-mls-tomcat\install.bat
    C:\install\cm7-cms-tomcat\install.bat
    C:\install\cm7-wfs-tomcat\install.bat
    C:\install\cm7-caefeeder-preview-tomcat\install.bat
    C:\install\cm7-caefeeder-live-tomcat\install.bat
    C:\install\cm7-studio-tomcat\install.bat
    C:\install\cm7-delivery-tomcat\install.bat
    cp Env:\OLD_JAVA_HOME Env:\JAVA_HOME
    rm Env:\OLD_JAVA_HOME

Start the Tomcats.

    Start-Service cm7-solr-master-tomcat
    Start-Service cm7-mls-tomcat
    Start-Service cm7-cms-tomcat
    Start-Service cm7-wfs-tomcat
    Start-Service cm7-caefeeder-preview-tomcat
    Start-Service cm7-caefeeder-live-tomcat
    Start-Service cm7-studio-tomcat
    Start-Service cm7-delivery-tomcat

Run the installation scripts for all command line tools.

    $tools = ls C:\install | where {$_ -match "tools"}
    foreach ($tool in $tools) { & "$($tool.FullName)\install.bat"  }

Extract, import and publish the content.

    &7z x -oC:\install C:\Users\Administrator\Downloads\content-users.zip
    C:\CoreMedia\cm7-cms-tools\bin\cm64 serverimport -u admin -p admin -r C:\install\content
    C:\CoreMedia\cm7-cms-tools\bin\cm64 bulkpublish -u admin -p admin -a -b -c

Install the Apache configuration.

    md C:\var\log\httpd
    C:\install\cm7-studio-apache\install.bat
    C:\install\cm7-delivery-apache\install.bat
    Add-Content "C:\Apache2.2\conf\httpd.conf" "Include conf.d/*.conf"
    Restart-Service Apache2.2

Add the Blueprint host names to be able to test the installation.

    Add-Content 'C:\Windows\System32\Drivers\etc\hosts' '127.0.0.1 studio-media.localhost studio-helios.localhost preview-media.localhost preview-helios.localhost editor.localhost media.localhost helios.localhost'

Now start a browser and open `https://studio-media.localhost/` and `http://media.localhost` to test the installation.


##SQL Server

To prepare the workspace for SQL Server you need to download and extract the SQL Server JDBC driver and install the
JAR to your Maven repository. Download the driver from [http://www.microsoft.com/en-us/download/details.aspx?id=11774]
and extract the files. Start a shell and change into the directory containing the extracted files, then execute:

    mvn install:install-file -Dfile="Microsoft JDBC Driver 4.0 for SQL Server\sqljdbc_4.0\enu\sqljdbc4.jar" -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar

The workspace contains scripts for creating database schemas and users in the
`workspace-configuration\database\sqlserver` directory.

Apply the following patch to prepare the workspace for a SQL server:

    diff --git a/modules/shared/database-drivers/pom.xml b/modules/shared/database-drivers/pom.xml
    index 1b12aea..914c969 100644
    --- a/modules/shared/database-drivers/pom.xml
    +++ b/modules/shared/database-drivers/pom.xml
    @@ -13,9 +13,9 @@

       <dependencies>
         <dependency>
    -      <groupId>mysql</groupId>
    -      <artifactId>mysql-connector-java</artifactId>
    -      <version>5.1.20</version>
    +      <groupId>com.microsoft.sqlserver</groupId>
    +      <artifactId>sqljdbc4</artifactId>
    +      <version>4.0</version>
         </dependency>
       </dependencies>

    diff --git a/packages/src/main/filters/default-deployment.properties b/packages/src/main/filters/default-deployment.prop
    index 9d44b92..254ee08 100644
    --- a/packages/src/main/filters/default-deployment.properties
    +++ b/packages/src/main/filters/default-deployment.properties
    @@ -12,15 +12,15 @@ configure.CMS_PERM=256m
     # document cache capacity (uses about 2kB heap per resource)
     configure.CMS_RESOURCECACHE_SIZE=60000
     # the jdbc database url
    -configure.CMS_DB_URL=jdbc:mysql://localhost:3306/cm7management
    +configure.CMS_DB_URL=jdbc:sqlserver://localhost;database=cm7management
     # the class of the jdbc driver
    -configure.CMS_DB_DRIVER=com.mysql.jdbc.Driver
    +configure.CMS_DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
     # the database user
     configure.CMS_DB_USER=cm7management
     # the database password
     configure.CMS_DB_PASSWORD=cm7management
     # the relative path to the db specific properties (below WEB-INF/properties)
    -configure.CMS_DB_PROPERTIES=corem/mysql
    +configure.CMS_DB_PROPERTIES=corem/mssql
     # the license file to read
     configure.CMS_LICENSE=properties/corem/license.zip

    @@ -33,15 +33,15 @@ configure.WFS_HEAP=512m
     # the JVM perm size used for -XX:PermSize and -XX:MaxPermSize
     configure.WFS_PERM=160m
     # the jdbc database url
    -configure.WFS_DB_URL=jdbc:mysql://localhost:3306/cm7management
    +configure.WFS_DB_URL=jdbc:sqlserver://localhost;database=cm7management
     # the class of the jdbc driver
    -configure.WFS_DB_DRIVER=com.mysql.jdbc.Driver
    +configure.WFS_DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
     # the database user
     configure.WFS_DB_USER=cm7management
     # the database password
     configure.WFS_DB_PASSWORD=cm7management
     # the relative path to the db specific properties (below WEB-INF/properties)
    -configure.WFS_DB_PROPERTIES=corem/mysql
    +configure.WFS_DB_PROPERTIES=corem/mssql

     # the host the server is deployed to
     configure.MLS_HOST=localhost
    @@ -54,15 +54,15 @@ configure.MLS_PERM=160m
     # document cache capacity (uses about 2kB heap per resource)
     configure.MLS_RESOURCECACHE_SIZE=60000
     # the jdbc database url
    -configure.MLS_DB_URL=jdbc:mysql://localhost:3306/cm7master
    +configure.MLS_DB_URL=jdbc:sqlserver://localhost;database=cm7master
     # the class of the jdbc driver
    -configure.MLS_DB_DRIVER=com.mysql.jdbc.Driver
    +configure.MLS_DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
     # the database user
     configure.MLS_DB_USER=cm7master
     # the database password
     configure.MLS_DB_PASSWORD=cm7master
     # the relative path to the db specific properties (below WEB-INF/properties)
    -configure.MLS_DB_PROPERTIES=corem/mysql
    +configure.MLS_DB_PROPERTIES=corem/mssql
     # the license file to read
     configure.MLS_LICENSE=properties/corem/license.zip

    @@ -80,15 +80,15 @@ configure.RLS_PERM=128m
     # document cache capacity (uses about 2kB heap per resource)
     configure.RLS_RESOURCECACHE_SIZE=60000
     # the jdbc database url
    -configure.RLS_DB_URL=jdbc:mysql://localhost:3306/cm7replication
    +configure.RLS_DB_URL=jdbc:sqlserver://localhost;database=cm7replication
     # the class of the jdbc driver
    -configure.RLS_DB_DRIVER=com.mysql.jdbc.Driver
    +configure.RLS_DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
     # the database user
     configure.RLS_DB_USER=cm7replication
     # the database password
     configure.RLS_DB_PASSWORD=cm7replication
     # the relative path to the db specific properties (below WEB-INF/properties)
    -configure.RLS_DB_PROPERTIES=corem/mysql
    +configure.RLS_DB_PROPERTIES=corem/mssql
     # the license file to read
     configure.RLS_LICENSE=properties/corem/license.zip

    @@ -149,9 +149,9 @@ configure.CAEFEEDER_PREVIEW_HEAP=512m
     # the JVM perm size used for -XX:PermSize and -XX:MaxPermSize
     configure.CAEFEEDER_PREVIEW_PERM=160m
     # the jdbc database url
    -configure.CAEFEEDER_PREVIEW_DB_URL=jdbc:mysql://localhost:3306/cm7mcaefeeder
    +configure.CAEFEEDER_PREVIEW_DB_URL=jdbc:sqlserver://localhost;database=cm7mcaefeeder
     # the class of the jdbc driver
    -configure.CAEFEEDER_PREVIEW_DB_DRIVER=com.mysql.jdbc.Driver
    +configure.CAEFEEDER_PREVIEW_DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
     # the database user
     configure.CAEFEEDER_PREVIEW_DB_USER=cm7mcaefeeder
     # the database password
    @@ -163,9 +163,9 @@ configure.CAEFEEDER_LIVE_HEAP=512m
     # the JVM perm size used for -XX:PermSize and -XX:MaxPermSize
     configure.CAEFEEDER_LIVE_PERM=160m
     # the jdbc database url
    -configure.CAEFEEDER_LIVE_DB_URL=jdbc:mysql://localhost:3306/cm7caefeeder
    +configure.CAEFEEDER_LIVE_DB_URL=jdbc:sqlserver://localhost;database=cm7caefeeder
     # the class of the jdbc driver
    -configure.CAEFEEDER_LIVE_DB_DRIVER=com.mysql.jdbc.Driver
    +configure.CAEFEEDER_LIVE_DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
     # the database user
     configure.CAEFEEDER_LIVE_DB_USER=cm7caefeeder
     # the database password

The scripts for creating and dropping databases require the SQL Server Command Line Utilities to be installed.
These tools require the SQL Server Native Client to be installed.

Download the tools for SQL Server 2008 R2 from the Microsoft web site.

    Start-BitsTransfer -Source "http://go.microsoft.com/fwlink/?LinkID=188401&clcid=0x409" -Destination .\Downloads\sqlncli.msi
    Start-BitsTransfer -Source "http://go.microsoft.com/fwlink/?LinkID=188430&clcid=0x409" -Destination .\Downloads\SqlCmdLnUtils.msi

Or download the tools for SQL Server 2012.

    Start-BitsTransfer -Source "http://go.microsoft.com/fwlink/?LinkID=239648&clcid=0x409" -Destination .\Downloads\sqlncli.msi
    Start-BitsTransfer -Source "http://go.microsoft.com/fwlink/?LinkID=239650&clcid=0x409" -Destination .\Downloads\SqlCmdLnUtils.msi

Install the tools.

    msiexec.exe /i "C:\Users\Administrator\Downloads\sqlncli.msi"
    msiexec.exe /i "C:\Users\Administrator\Downloads\SqlCmdLnUtils.msi"
    $env:PATH="$env:PATH;$env:ProgramFiles\Microsoft SQL Server\110\Tools\Binn"
    [Environment]::SetEnvironmentVariable("PATH", "$env:PATH", "Machine")

When the SQL Server is running on a remote host, several environment variables have to be set for the SQL Server Command
Line Utilities. Replace the text in `<` and `>` brackets with your SQL Server credentials.

    $env:SQLCMDUSER="<YOUR_SQL_SERVER_USER>"
    $env:SQLCMDPASSWORD="<YOUR_SQL_SERVER_PASSWORD>"
    $env:SQLCMDSERVER="<YOUR_SQL_SERVER_HOST>"
    [Environment]::SetEnvironmentVariable("SQLCMDUSER", "$env:SQLCMDUSER", "Machine")
    [Environment]::SetEnvironmentVariable("SQLCMDPASSWORD", "$env:SQLCMDPASSWORD", "Machine")
    [Environment]::SetEnvironmentVariable("SQLCMDSERVER", "$env:SQLCMDSERVER", "Machine")

Now proceed as described above in the MySQL setup, but skip the MySQL installation and run
`.\Downloads\database\sqlserver\createDB.bat` instead of `.\Downloads\database\mysql\createDB.bat`.
