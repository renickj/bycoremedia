### Logging ###########################
ErrorLog @STUDIO_APACHE_LOG_DIR@/studio-error.log
LogLevel warn
CustomLog @STUDIO_APACHE_LOG_DIR@/studio-access.log combined

