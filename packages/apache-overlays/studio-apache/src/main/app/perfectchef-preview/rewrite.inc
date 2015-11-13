RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/perfectchef-preview-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

# Internal services, served only by Tomcat. Deny external invocation.
RewriteCond %{REQUEST_URI} ^/internal/(.*)
RewriteRule ^/internal/(.*) - [F,PT,L]

RewriteCond %{REQUEST_URI} ^/blueprint/servlet/internal/(.*)
RewriteRule ^/blueprint/servlet/internal/(.*) - [F,PT,L]

# robots.txt
RewriteCond %{REQUEST_URI} ^/robots.txt
RewriteRule ^/robots.txt /blueprint/servlet/service/robots/perfectchef [PT,L]

# Send empty URL to perfectchef home page
RewriteCond %{REQUEST_URI} ^/$
RewriteRule ^/$ /blueprint/servlet/perfectchef [R=302,L]
