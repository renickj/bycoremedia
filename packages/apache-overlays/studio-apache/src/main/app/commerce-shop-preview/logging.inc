### Logging ###########################
ErrorLog @STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-error.log
LogLevel warn
CustomLog @STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-access.log combined
