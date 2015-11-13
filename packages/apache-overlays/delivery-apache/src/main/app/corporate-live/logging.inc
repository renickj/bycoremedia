### Logging ###########################
ErrorLog @DELIVERY_APACHE_LOG_DIR@/corporate-live-error.log
LogLevel warn
CustomLog @DELIVERY_APACHE_LOG_DIR@/corporate-live-access.log combined
