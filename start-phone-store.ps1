#Requires -Version 5.1
param(
    [string] $MysqlPassword = "",
    [switch] $SkipDatabaseInit,
    [switch] $WithCms
)

$Script = Join-Path $PSScriptRoot "scripts\start-phone-store.ps1"
if (-not (Test-Path $Script)) {
    throw "Khong tim thay script khoi dong: $Script"
}

& $Script -MysqlPassword $MysqlPassword -SkipDatabaseInit:$SkipDatabaseInit -WithCms:$WithCms
