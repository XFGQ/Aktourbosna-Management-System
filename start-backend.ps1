$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"

# Kill whatever is on port 8080
$conn = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($conn) {
    Write-Host "Stopping process on port 8080 (PID $($conn.OwningProcess))..."
    Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 2
}

Write-Host "Starting backend..."
Set-Location "$PSScriptRoot\tur-backend"
mvn spring-boot:run
