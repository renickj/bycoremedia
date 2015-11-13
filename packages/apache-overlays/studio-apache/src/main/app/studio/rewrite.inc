RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/studio-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

#RewriteCond %{HTTPS} off
#RewriteRule (.*) https://%{HTTP_HOST}%{REQUEST_URI}

#
# CM_ENVIRONMENT is used to determin if the apache runs in a production environment or a development
# environment. For development, the studio cannot be accessed under /studio, so the corresponding
# RewriteRule must be deactivated.
#
SetEnvIf Request_URI ".*" CM_ENVIRONMENT=@STUDIO_ENVIRONMENT@
RewriteCond %{ENV:CM_ENVIRONMENT} production
RewriteCond %{REQUEST_URI} ^/$
RewriteRule ^/$ /studio/$1 [R]
