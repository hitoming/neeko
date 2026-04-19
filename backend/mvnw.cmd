@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@REM Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%

@REM Find the project base dir
set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%
if "%MAVEN_PROJECTBASEDIR%"=="" set MAVEN_PROJECTBASEDIR=%APP_HOME%

@REM Add default JVM options here
set DEFAULT_JVM_OPTS="-Xmx64m" "-Xms64m"

@REM Maven wrapper download URL
set MAVEN_DOWNLOAD_URL=https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
set MAVEN_TEMP_DIR=%TEMP%\maven-%RANDOM%

@REM Download Maven if not found
if not exist "%MAVEN_PROJECTBASEDIR%\maven" (
    echo Downloading Maven...
    powershell -Command "Invoke-WebRequest -Uri '%MAVEN_DOWNLOAD_URL%' -OutFile '%MAVEN_TEMP_DIR%.zip'"
    powershell -Command "Expand-Archive -Path '%MAVEN_TEMP_DIR%.zip' -DestinationPath '%MAVEN_PROJECTBASEDIR%' -Force"
    powershell -Command "Move-Item -Path '%MAVEN_PROJECTBASEDIR%\apache-maven-*' -Destination '%MAVEN_PROJECTBASEDIR%\maven' -Force"
    del /Q "%MAVEN_TEMP_DIR%.zip" 2>nul
)

@REM Set MAVEN_HOME
set MAVEN_HOME=%MAVEN_PROJECTBASEDIR%\maven
set MAVEN_BIN=%MAVEN_HOME%\bin\mvn.cmd

@REM Execute Maven
"%MAVEN_BIN%" %*
