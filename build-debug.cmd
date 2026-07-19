@echo off
setlocal

call "%~dp0gradlew.bat" --no-daemon :app:assembleDebug
exit /b %ERRORLEVEL%
