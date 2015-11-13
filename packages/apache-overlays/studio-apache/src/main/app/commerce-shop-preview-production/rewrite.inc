RewriteEngine on
RewriteLog "@STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-production-rewrite.log"
RewriteLogLevel @STUDIO_APACHE_REWRITE_LOGLEVEL@

# Send empty URL to aurora home page preview
RewriteCond %{REQUEST_URI} ^/$
RewriteRule ^/$ /webapp/wcs/stores/servlet/eboots-uk [R=302,L]
