@echo off
set "JAVA_HOME=C:\Users\can91\.jdks\ms-21.0.10"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MVN=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd"

echo Starting Aktour ViaBalkan Frontend...
cd /d "%~dp0tur-frontend"
"%MVN%" javafx:run
