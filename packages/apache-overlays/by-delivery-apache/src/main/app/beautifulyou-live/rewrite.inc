RewriteEngine on
RewriteLog "@DELIVERY_APACHE_LOG_DIR@/perfectchef-live-rewrite.log"
RewriteLogLevel @DELIVERY_APACHE_REWRITE_LOGLEVEL@

# Remove preview querystring
RewriteCond %{QUERY_STRING} ^.*view=fragmentPreview.*$
RewriteRule (.*) $1? [R=301,L]

# Internal services, served only by Tomcat. Deny external invocation.
RewriteCond %{REQUEST_URI} ^/internal/(.*)
RewriteRule ^/internal/(.*) - [F,PT,L]

RewriteCond %{REQUEST_URI} ^/blueprint/servlet/internal/(.*)
RewriteRule ^/blueprint/servlet/internal/(.*) - [F,PT,L]

# robots.txt
RewriteCond %{REQUEST_URI} ^/robots.txt
RewriteRule ^/robots.txt /blueprint/servlet/service/robots/beautifulyou [PT,L]

# Send empty URL to perfectchef home page
RewriteCond %{REQUEST_URI} ^/$
RewriteRule ^/$ /beautifulyou [R=302,L]
