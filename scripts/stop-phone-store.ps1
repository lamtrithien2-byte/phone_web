#Requires -Version 5.1
$ErrorActionPreference = "SilentlyContinue"

function Stop-Port {
    param([int] $Port)

    $processId = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
        Select-Object -First 1 -ExpandProperty OwningProcess
    if ($processId) {
        Write-Host "Dung tien trinh tren :$Port (PID $processId)"
        Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
    }
}

Stop-Port -Port 8081
Stop-Port -Port 8082
Stop-Port -Port 8083
