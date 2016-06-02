### Expire Headers for images, css, js #########
<IfModule mod_expires.c>
  ExpiresActive On
  ExpiresByType image/gif "access plus 1 day"
  ExpiresByType image/jpg "access plus 1 day"
  ExpiresByType image/jpeg "access plus 1 day"
  ExpiresByType image/png "access plus 1 day"
  ExpiresByType image/vnd.microsoft.icon "access plus 1 month"
  ExpiresByType image/x-icon "access plus 1 months"
  ExpiresByType text/css "access plus 1 day"
  ExpiresByType text/javascript "access plus 1 day"
  ExpiresByType application/javascript "access plus 1 day"
</IfModule>
