RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/corporate-preview-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

# Internal services, served only by Tomcat. Deny external invocation.
RewriteCond %{REQUEST_URI} ^/internal/(.*)
RewriteRule ^/internal/(.*) - [F,PT,L]

RewriteCond %{REQUEST_URI} ^/blueprint/servlet/internal/(.*)
RewriteRule ^/blueprint/servlet/internal/(.*) - [F,PT,L]

# robots.txt
RewriteCond %{REQUEST_URI} ^/robots.txt
RewriteRule ^/robots.txt /blueprint/servlet/service/robots/corporate [PT,L]

# Send empty URL to corporate home page
RewriteCond %{REQUEST_URI} ^/$
RewriteRule ^/$ /blueprint/servlet/corporate [R=302,L]
