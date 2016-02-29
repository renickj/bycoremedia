### Logging ###########################
ErrorLog @DELIVERY_APACHE_LOG_DIR@/beautifulyou-live-error.log
LogLevel warn
CustomLog @DELIVERY_APACHE_LOG_DIR@/beautifulyou-live-access.log combined

