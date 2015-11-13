@ECHO OFF
REM First parameter is the install root
REM All paramters are optional

SETLOCAL ENABLEEXTENSIONS
IF ERRORLEVEL 1 ECHO Unable to enable extensions

SET MY_DIR=%~dp0
SET INSTALL_ROOT_PARAM=%1

IF DEFINED INSTALL_ROOT_PARAM (
  SET INSTALL_ROOT=%INSTALL_ROOT_PARAM%
) ELSE (
  SET INSTALL_ROOT=@INSTALL_ROOT@
)
SET INSTALL_ROOT=%INSTALL_ROOT:/=\%

REM Must NOT contain whitespaces, must NOT be enclosed in ""
SET SERVICE_NAME=@APPLICATION_NAME@

REM By convention CATALINA_HOME is set to @INSTALL_ROOT@\cm7-tomcat-installation. Must NOT be enclosed in ""
SET CATALINA_HOME=@INSTALL_ROOT@/@APPLICATION_PREFIX@-tomcat-installation

REM Remove the service
CALL %CATALINA_HOME%\bin\service.bat remove %SERVICE_NAME%

ENDLOCAL
