### Logging ###########################
ErrorLog @STUDIO_APACHE_LOG_DIR@/beautifulyou-preview-error.log
LogLevel warn
CustomLog @STUDIO_APACHE_LOG_DIR@/beautifulyou-preview-access.log combined
