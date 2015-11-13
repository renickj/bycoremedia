RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

RewriteCond %{REQUEST_URI} ^/lobtools/(.*)
RewriteRule ^/lobtools/(.*) /lobtools/$1 [PT,L]

# Send empty URL to aurora home page
RewriteCond %{REQUEST_URI} ^/$
RewriteRule ^/$ /webapp/wcs/stores/servlet/eboots-uk [R=302,L]
