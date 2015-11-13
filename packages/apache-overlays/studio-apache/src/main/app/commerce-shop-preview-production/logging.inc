### Logging ###########################
ErrorLog @STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-production-error.log
LogLevel warn
CustomLog @STUDIO_APACHE_LOG_DIR@/commerce-shop-preview-production-access.log combined
