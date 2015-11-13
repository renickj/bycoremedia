@echo off
setlocal

SET MY_DIR=%~dp0

rem Must NOT contain whitespaces, must NOT be enclosed in ""
set ServiceName=@APPLICATION_NAME@

REM By convention CATALINA_HOME is set to %MY_DIR%\..\cm7-tomcat-installation. Must NOT be enclosed in ""
SET CATALINA_HOME=%MY_DIR%\..\@APPLICATION_PREFIX@-tomcat-installation
ECHO CATALINA_HOME is set to %CATALINA_HOME%

rem Edit the Service
%CATALINA_HOME%\bin\tomcat7w.exe //ES//%ServiceName%

endlocal
