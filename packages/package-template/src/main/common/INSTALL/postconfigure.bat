@ECHO OFF
REM First parameter is the configure root
REM All paramters are optional

SETLOCAL ENABLEEXTENSIONS
IF ERRORLEVEL 1 ECHO Unable to enable extensions
SET CONFIGURE_ROOT_PARAM=%1

IF DEFINED CONFIGURE_ROOT_PARAM (
 SET CONFIGURE_ROOT=%CONFIGURE_ROOT_PARAM%
) ELSE (
  SET CONFIGURE_ROOT=@CONFIGURE_ROOT@
)
SET CONFIGURE_ROOT=%CONFIGURE_ROOT:/=\%

REM target dir is the parent dir of the location of this batch file, i.e. INSTALL\..
SET MY_DIR=%~dp0
SET TARGET_DIR=%MY_DIR%..

SET CONFIGURATION_FILE=%CONFIGURE_ROOT%/@APPLICATION_NAME@.properties

IF @postconfiguration.enable@==true (
  ECHO Service in %TARGET_DIR% will be configured with %CONFIGURATION_FILE%.

  "%JAVA_HOME%\bin\java" -jar "%MY_DIR%token-replacer-onejar.jar" -f "%CONFIGURATION_FILE%" -d "%TARGET_DIR%"
)

ENDLOCAL
