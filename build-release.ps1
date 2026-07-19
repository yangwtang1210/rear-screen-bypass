$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

Push-Location $projectRoot
try {
    & ".\gradlew.bat" --no-daemon :app:assembleRelease
} finally {
    Pop-Location
}
