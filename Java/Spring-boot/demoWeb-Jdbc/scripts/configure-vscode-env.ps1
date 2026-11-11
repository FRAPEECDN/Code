& (Join-Path $PSScriptRoot "run-with-docker-db.ps1") -ConfigureVSCode
if (-not $?) {
    exit 1
}
