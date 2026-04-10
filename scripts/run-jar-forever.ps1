#Requires -Version 5.1
param(
    [Parameter(Mandatory = $true)]
    [string] $ServiceName,

    [Parameter(Mandatory = $true)]
    [string] $JarPath,

    [Parameter(Mandatory = $true)]
    [string] $WorkingDirectory,

    [string] $JavaHome = "",

    [int] $RestartDelaySec = 5
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path $JarPath)) {
    Write-Error "Khong tim thay JAR: $JarPath"
}

if (-not (Test-Path $WorkingDirectory)) {
    Write-Error "Khong tim thay working directory: $WorkingDirectory"
}

if ($JavaHome) {
    $env:JAVA_HOME = $JavaHome
    $env:PATH = "$JavaHome\bin;$env:PATH"
}

$javaExe = Join-Path $env:JAVA_HOME "bin\javaw.exe"
if (-not (Test-Path $javaExe)) {
    $javaExe = Join-Path $env:JAVA_HOME "bin\java.exe"
}
if (-not (Test-Path $javaExe)) {
    $javaExe = "javaw.exe"
}
if (-not (Get-Command $javaExe -ErrorAction SilentlyContinue)) {
    $javaExe = "java.exe"
}

$logDir = Join-Path $WorkingDirectory "logs"
New-Item -ItemType Directory -Force -Path $logDir | Out-Null
$stdoutLog = Join-Path $logDir ($ServiceName + ".out.log")
$stderrLog = Join-Path $logDir ($ServiceName + ".err.log")

Set-Location $WorkingDirectory
Write-Host "=== $ServiceName ==="
Write-Host "JAR  : $JarPath"
Write-Host "OUT  : $stdoutLog"
Write-Host "ERR  : $stderrLog"
Write-Host "JAVA : $javaExe"
Write-Host ""

while ($true) {
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Add-Content -Path $stdoutLog -Value "[$timestamp] START $ServiceName"
    try {
        $quotedJarPath = '"' + $JarPath + '"'
        $process = Start-Process -FilePath $javaExe `
            -ArgumentList "-jar $quotedJarPath" `
            -WorkingDirectory $WorkingDirectory `
            -RedirectStandardOutput $stdoutLog `
            -RedirectStandardError $stderrLog `
            -PassThru
        $process.WaitForExit()
        $exitCode = $process.ExitCode
    } catch {
        $_ | Out-String | Add-Content -Path $stderrLog
        $exitCode = -1
    }
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    Add-Content -Path $stdoutLog -Value "[$timestamp] EXIT $ServiceName (code=$exitCode). Restart sau $RestartDelaySec giay..."
    Start-Sleep -Seconds $RestartDelaySec
}
