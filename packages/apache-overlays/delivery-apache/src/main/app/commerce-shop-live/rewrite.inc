RewriteEngine on
RewriteLog "@DELIVERY_APACHE_LOG_DIR@/commerce-shop-rewrite.log"
RewriteLogLevel @DELIVERY_APACHE_REWRITE_LOGLEVEL@

# Send empty URL to aurora home page
RewriteCond %{REQUEST_URI} ^/$
RewriteRule ^/$ /webapp/wcs/stores/servlet/eboots-uk [R=302,L]
