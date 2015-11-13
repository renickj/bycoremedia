RewriteCond %{REQUEST_URI} ^/editor-webstart/(.*)
RewriteRule ^/editor-webstart/(.*) /editor-webstart/$1 [PT,L]

RewriteCond %{REQUEST_URI} ^/coremedia/(.*)
RewriteRule ^/coremedia/(.*) http://@CMS_HOST@:@CMS_HTTP_PORT@/coremedia/$1 [P,L]

RewriteCond %{REQUEST_URI} ^/workflow/(.*)
RewriteRule ^/workflow/(.*) http://@WFS_HOST@:@WFS_HTTP_PORT@/workflow/$1 [P,L]

RewriteCond %{REQUEST_URI} ^/(.*)
RewriteRule ^/(.*) /editor-webstart/$1 [PT,L]