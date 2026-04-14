#Requires -Version 5.1
param(
    [switch] $WithCms
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$Root = Split-Path -Parent $ScriptDir

function Find-JdkHome {
    $jdkUserHome = Join-Path $env:USERPROFILE ".jdks"
    $userJdks = @()
    if (Test-Path $jdkUserHome) {
        $userJdks = Get-ChildItem $jdkUserHome -Directory -ErrorAction SilentlyContinue |
            Sort-Object Name -Descending |
            Select-Object -ExpandProperty FullName
    }
    $candidates = @(
        "$env:JAVA_HOME",
        $userJdks,
        "C:\Program Files\Java\jdk-26",
        "C:\Program Files\Java\jdk-21",
        "C:\Program Files\Java\jdk-17",
        "C:\Program Files\Eclipse Adoptium\jdk-21.0.5.11-hotspot",
        "C:\Program Files\Microsoft\jdk-17.0.13.11-hotspot"
    ) | Where-Object { $_ -and (Test-Path "$_\bin\java.exe") }
    $resolved = @($candidates)
    if ($resolved.Count -ge 1) { return $resolved[0] }
    return $null
}

function Test-PortOpen {
    param([int] $Port)
    try {
        $c = Test-NetConnection -ComputerName localhost -Port $Port -WarningAction SilentlyContinue
        return $c.TcpTestSucceeded
    } catch { return $false }
}

function Wait-Port {
    param([int] $Port, [int] $TimeoutSec = 90)
    $deadline = (Get-Date).AddSeconds($TimeoutSec)
    while ((Get-Date) -lt $deadline) {
        if (Test-PortOpen -Port $Port) { return $true }
        Start-Sleep -Seconds 1
    }
    return $false
}

function Get-PortProcess {
    param([int] $Port)
    try {
        return Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
            Select-Object -First 1 -ExpandProperty OwningProcess
    } catch {
        return $null
    }
}

function Start-JavaWindow {
    param(
        [string] $Title,
        [string] $JarPath,
        [string] $WorkingDirectory
    )

    $runnerScript = Join-Path $ScriptDir "run-jar-forever.ps1"
    $command = "& '$runnerScript' -ServiceName '$Title' -JarPath '$JarPath' -WorkingDirectory '$WorkingDirectory' -JavaHome '$env:JAVA_HOME'"
    Start-Process -FilePath "powershell.exe" -ArgumentList @(
        "-ExecutionPolicy", "Bypass",
        "-Command", $command
    ) -WorkingDirectory $WorkingDirectory -WindowStyle Hidden | Out-Null
}

$jdk = Find-JdkHome
if (-not $jdk) {
    Write-Error "Khong tim thay JDK. Cai JDK 17+ va dat bien moi truong JAVA_HOME."
}
$env:JAVA_HOME = $jdk
$env:PATH = "$jdk\bin;$env:PATH"
Write-Host "JAVA_HOME = $jdk" -ForegroundColor Cyan

$coreJar = Get-ChildItem -Path (Join-Path $Root "core-api\target") -Filter "phone-store-core-api-*.jar" -File -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1
$shopJar = Get-ChildItem -Path (Join-Path $Root "ecommerce-web\target") -Filter "phone-store-ecommerce-web-*.jar" -File -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1
$cmsJar = Get-ChildItem -Path (Join-Path $Root "cms-web\target") -Filter "phone-store-cms-web-*.jar" -File -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-not $coreJar -or -not $shopJar) {
    Write-Error "Khong tim thay JAR. Hay build truoc bang start-phone-store.ps1."
}

if (-not (Test-PortOpen -Port 8081)) {
    Write-Host "Khoi dong core-api :8081 ..." -ForegroundColor Cyan
    Start-JavaWindow -Title "phone-store-core-api" -JarPath $coreJar.FullName -WorkingDirectory $Root
    if (-not (Wait-Port -Port 8081 -TimeoutSec 120)) {
        Write-Error "core-api khong mo duoc cong 8081."
    }
} else {
    Write-Host "core-api dang chay tren :8081 (PID $(Get-PortProcess -Port 8081))." -ForegroundColor DarkYellow
}

if (-not (Test-PortOpen -Port 8083)) {
    Write-Host "Khoi dong ecommerce-web :8083 ..." -ForegroundColor Cyan
    Start-JavaWindow -Title "phone-store-ecommerce-web" -JarPath $shopJar.FullName -WorkingDirectory $Root
    if (-not (Wait-Port -Port 8083 -TimeoutSec 120)) {
        Write-Error "ecommerce-web khong mo duoc cong 8083."
    }
} else {
    Write-Host "ecommerce-web dang chay tren :8083 (PID $(Get-PortProcess -Port 8083))." -ForegroundColor DarkYellow
}

if ($WithCms) {
    if (-not $cmsJar) {
        Write-Error "Khong tim thay cms-web JAR."
    }
    if (-not (Test-PortOpen -Port 8082)) {
        Write-Host "Khoi dong cms-web :8082 ..." -ForegroundColor Cyan
        Start-JavaWindow -Title "phone-store-cms-web" -JarPath $cmsJar.FullName -WorkingDirectory $Root
        if (-not (Wait-Port -Port 8082 -TimeoutSec 120)) {
            Write-Error "cms-web khong mo duoc cong 8082."
        }
    } else {
        Write-Host "cms-web dang chay tren :8082 (PID $(Get-PortProcess -Port 8082))." -ForegroundColor DarkYellow
    }
}

Write-Host ""
Write-Host "=== Runtime da chay ===" -ForegroundColor Green
Write-Host "  API :  http://localhost:8081/api/products"
Write-Host "  Shop:  http://localhost:8083/products"
if ($WithCms) {
    Write-Host "  CMS :  http://localhost:8082/login"
}
