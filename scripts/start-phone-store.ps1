#Requires -Version 5.1
<#
  Build and run Phone Store: core-api (8081) + ecommerce-web (8083).
  Optional: cms-web (8082) with -WithCms

  Prerequisites: JDK 17+, MySQL Server running on localhost:3306, root password matching core-api (empty by default).

  Usage (PowerShell):
    cd scripts
    .\start-phone-store.ps1

  With MySQL root password:
    .\start-phone-store.ps1 -MysqlPassword "yourpassword"

  Skip re-importing DB:
    .\start-phone-store.ps1 -SkipDatabaseInit
#>
param(
    [string] $MysqlPassword = "",
    [switch] $SkipDatabaseInit,
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

function Find-MysqlExe {
    $paths = @(
        "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe",
        "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe",
        "C:\xampp\mysql\bin\mysql.exe"
    )
    foreach ($p in $paths) {
        if (Test-Path $p) { return $p }
    }
    $w = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if ($w) { return $w.Source }
    return $null
}

function Test-PortOpen {
    param([int] $Port)
    try {
        $c = Test-NetConnection -ComputerName 127.0.0.1 -Port $Port -WarningAction SilentlyContinue
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

function Stop-ProcessOnPort {
    param([int] $Port)

    $processId = Get-PortProcess -Port $Port
    if (-not $processId) { return }

    Write-Host "Dung tien trinh cu tren :$Port (PID $processId) truoc khi build..." -ForegroundColor Yellow
    try {
        Stop-Process -Id $processId -Force -ErrorAction Stop
    } catch {
        Write-Warning "Khong the dung PID $processId tren cong ${Port}: $($_.Exception.Message)"
    }
}

$jdk = Find-JdkHome
if (-not $jdk) {
    Write-Error "Khong tim thay JDK. Cai JDK 17+ va dat bien moi truong JAVA_HOME."
}
$env:JAVA_HOME = $jdk
$env:PATH = "$jdk\bin;$env:PATH"
Write-Host "JAVA_HOME = $jdk" -ForegroundColor Cyan
Set-Location $Root

$mvnw = Join-Path $Root "mvnw.cmd"
if (-not (Test-Path $mvnw)) {
    Write-Error "Khong thay mvnw.cmd tai: $Root"
}

# Try start MySQL Windows service if port 3306 is closed
if (-not (Test-PortOpen -Port 3306)) {
    $svc = Get-Service -ErrorAction SilentlyContinue | Where-Object {
        $_.Name -match 'mysql' -or $_.DisplayName -match 'MySQL'
    } | Select-Object -First 1
    if ($svc -and $svc.Status -ne 'Running') {
        Write-Host "Dang khoi dong dich vu: $($svc.Name)..." -ForegroundColor Yellow
        try {
            Start-Service -Name $svc.Name
            Start-Sleep -Seconds 3
        } catch {
            Write-Warning "Khong the Start-Service (can quyen Admin): $($_.Exception.Message)"
        }
    }
}

if (-not (Test-PortOpen -Port 3306)) {
    Write-Host @"
Loi: MySQL chua lang nghe tren 127.0.0.1:3306.
- Win+R → services.msc → tim MySQL / MySQL84 / MySQL80 → Start
- Hoac mo MySQL Installer (Start Menu) de hoan tat cau hinh Server
core-api can MySQL nen script dung tai day.
"@ -ForegroundColor Red
    exit 1
}

$mysql = Find-MysqlExe
if (-not $SkipDatabaseInit -and $mysql) {
    $sqlFile = Join-Path $ScriptDir "phone_store_full_setup.sql"
    Write-Host "Import database tu: $sqlFile" -ForegroundColor Cyan
    $mysqlCliArgs = @("-u", "root", "--default-character-set=utf8mb4")
    if ($MysqlPassword) {
        $mysqlCliArgs += "-p$MysqlPassword"
    }
    Get-Content -LiteralPath $sqlFile -Raw -Encoding UTF8 | & $mysql @mysqlCliArgs
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Loi khi import SQL. Kiem tra mat khau root MySQL; thu: .\start-phone-store.ps1 -MysqlPassword '...'"
    }
    Write-Host "Database phone_store da san sang." -ForegroundColor Green
} elseif (-not $SkipDatabaseInit) {
    Write-Error "Khong tim thay mysql.exe de import DB. Cai MySQL Server hoac them mysql vao PATH."
}
if ($SkipDatabaseInit) {
    Write-Host "Bo qua import SQL (-SkipDatabaseInit)." -ForegroundColor DarkGray
}

Stop-ProcessOnPort -Port 8081
Stop-ProcessOnPort -Port 8083
if ($WithCms) {
    Stop-ProcessOnPort -Port 8082
}
Start-Sleep -Seconds 2

Write-Host "Dang build Maven (core-api, ecommerce-web$(if ($WithCms) { ', cms-web' }))..." -ForegroundColor Cyan
$modules = "core-api,ecommerce-web"
if ($WithCms) { $modules = "core-api,ecommerce-web,cms-web" }
$wrapperJar = Join-Path $Root ".mvn\wrapper\maven-wrapper.jar"
Push-Location $Root
& (Join-Path $env:JAVA_HOME "bin\java.exe") `
    "-Dmaven.multiModuleProjectDirectory=$Root" `
    "-classpath" $wrapperJar `
    "org.apache.maven.wrapper.MavenWrapperMain" `
    "-q" "-DskipTests" "package" "-pl" $modules "-am"
if ($LASTEXITCODE -ne 0) {
    Pop-Location
    Write-Error "Maven build that bai."
}
Pop-Location

$coreJar = Get-ChildItem -Path (Join-Path $Root "core-api\target") -Filter "phone-store-core-api-*.jar" -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1
$shopJar = Get-ChildItem -Path (Join-Path $Root "ecommerce-web\target") -Filter "phone-store-ecommerce-web-*.jar" -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1

if (-not $coreJar -or -not $shopJar) {
    Write-Error "Khong tim thay file JAR sau build."
}

if (Test-PortOpen -Port 8081) {
    Write-Host "core-api da dang chay tren :8081 (PID $(Get-PortProcess -Port 8081))." -ForegroundColor DarkYellow
} else {
    Write-Host "Khoi dong core-api :8081 ..." -ForegroundColor Cyan
    Start-JavaWindow -Title "phone-store-core-api" -JarPath $coreJar.FullName -WorkingDirectory $Root
    if (-not (Wait-Port -Port 8081 -TimeoutSec 120)) {
        Write-Error "core-api khong mo duoc cong 8081. Xem cua so cmd/java vua mo."
    }
}

if (Test-PortOpen -Port 8083) {
    Write-Host "ecommerce-web da dang chay tren :8083 (PID $(Get-PortProcess -Port 8083))." -ForegroundColor DarkYellow
} else {
    Write-Host "Khoi dong ecommerce-web :8083 ..." -ForegroundColor Cyan
    Start-JavaWindow -Title "phone-store-ecommerce-web" -JarPath $shopJar.FullName -WorkingDirectory $Root
    if (-not (Wait-Port -Port 8083 -TimeoutSec 120)) {
        Write-Error "ecommerce-web khong mo duoc cong 8083. Xem cua so cmd/java vua mo."
    }
}

if ($WithCms) {
    $cmsJar = Get-ChildItem -Path (Join-Path $Root "cms-web\target") -Filter "phone-store-cms-web-*.jar" -File | Sort-Object LastWriteTime -Descending | Select-Object -First 1
    if ($cmsJar) {
        if (Test-PortOpen -Port 8082) {
            Write-Host "cms-web da dang chay tren :8082 (PID $(Get-PortProcess -Port 8082))." -ForegroundColor DarkYellow
        } else {
            Write-Host "Khoi dong cms-web :8082 ..." -ForegroundColor Cyan
            Start-JavaWindow -Title "phone-store-cms-web" -JarPath $cmsJar.FullName -WorkingDirectory $Root
            Wait-Port -Port 8082 -TimeoutSec 90 | Out-Null
        }
    }
}

Write-Host ""
Write-Host "=== Da chay xong ===" -ForegroundColor Green
Write-Host "  Shop (khach):   http://127.0.0.1:8083/"
Write-Host "  API:            http://127.0.0.1:8081/api/products"
if ($WithCms) {
    Write-Host "  CMS (quan tri): http://127.0.0.1:8082/login  (admin / admin123 - lan dau dang nhap vai tro Admin)"
}
Write-Host ""
Write-Host "Tai khoan mau DB: admin/admin123 (admin), staff1/staff123 (nhan vien CMS)." -ForegroundColor DarkGray
