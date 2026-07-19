@echo off
setlocal

call "%~dp0gradlew.bat" --no-daemon :app:assembleRelease
exit /b %ERRORLEVEL%
