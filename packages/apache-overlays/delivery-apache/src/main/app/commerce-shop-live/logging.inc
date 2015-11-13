### Logging ###########################
ErrorLog @DELIVERY_APACHE_LOG_DIR@/commerce-shop-error.log
LogLevel warn
CustomLog @DELIVERY_APACHE_LOG_DIR@/commerce-shop-access.log combined
