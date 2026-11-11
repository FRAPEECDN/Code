param(
    [int]$ServerPort = 8081,
    [switch]$ConfigureVSCode
)

$ErrorActionPreference = "Stop"
$workspaceRoot = Split-Path -Parent $PSScriptRoot
$mavenWrapper = Join-Path $workspaceRoot "mvnw.cmd"

if ([string]::IsNullOrWhiteSpace($env:JASYPT_ENCRYPTOR_PASSWORD)) {
    throw "Set JASYPT_ENCRYPTOR_PASSWORD in the process environment; do not put it in .env."
}

Push-Location $workspaceRoot
$applicationExitCode = $null
try {
    docker compose up -d --wait database
    if ($LASTEXITCODE -ne 0) {
        throw "Docker Compose could not start PostgreSQL. Check DB_NAME, DB_USERNAME, DB_PASSWORD, and Docker."
    }

    $containerId = docker compose ps -q database
    if ($LASTEXITCODE -ne 0 -or [string]::IsNullOrWhiteSpace($containerId)) {
        throw "Docker Compose did not return the PostgreSQL container ID."
    }

    $containerJson = docker inspect $containerId
    if ($LASTEXITCODE -ne 0) {
        throw "Could not inspect the PostgreSQL container."
    }

    $container = $containerJson | ConvertFrom-Json
    $settings = @{}
    foreach ($entry in $container[0].Config.Env) {
        $parts = $entry -split "=", 2
        if ($parts.Length -eq 2) {
            $settings[$parts[0]] = $parts[1]
        }
    }

    $database = $settings["POSTGRES_DB"]
    $username = $settings["POSTGRES_USER"]
    $password = $settings["POSTGRES_PASSWORD"]
    if ([string]::IsNullOrWhiteSpace($database) -or
        [string]::IsNullOrWhiteSpace($username) -or
        [string]::IsNullOrWhiteSpace($password)) {
        throw "PostgreSQL must define POSTGRES_DB, POSTGRES_USER, and POSTGRES_PASSWORD."
    }

    $portMapping = docker compose port database 5432
    if ($LASTEXITCODE -ne 0 -or -not $portMapping) {
        throw "Docker Compose did not publish PostgreSQL port 5432."
    }
    $hostPort = [int](($portMapping | Select-Object -First 1) -split ":")[-1]

    $env:DB_URL = "jdbc:postgresql://localhost:$hostPort/$database"
    $env:DB_USERNAME = $username
    $env:DB_PASSWORD = $password
    $encryptedLines = & $mavenWrapper "-q" "-Dspring-boot.run.main-class=com.fp.boot_demo_web.config.DatabaseCredentialEncryptor" "spring-boot:run"
    if ($LASTEXITCODE -ne 0) {
        throw "Could not encrypt PostgreSQL credentials for the application."
    }

    $encryptedUsername = $encryptedLines | Where-Object { "$_" -match "^DB_USERNAME=ENC\(.+\)$" } | Select-Object -Last 1
    $encryptedPassword = $encryptedLines | Where-Object { "$_" -match "^DB_PASSWORD=ENC\(.+\)$" } | Select-Object -Last 1
    if (-not $encryptedUsername -or -not $encryptedPassword) {
        throw "The credential encryptor did not return both encrypted database values."
    }

    $env:DB_USERNAME = "$encryptedUsername" -replace "^DB_USERNAME=", ""
    $env:DB_PASSWORD = "$encryptedPassword" -replace "^DB_PASSWORD=", ""
    $env:SERVER_PORT = "$ServerPort"

    if ($ConfigureVSCode) {
        $launchEnvPath = Join-Path $workspaceRoot ".vscode\app.env"
        @(
            "DB_URL=$env:DB_URL"
            "DB_USERNAME=$env:DB_USERNAME"
            "DB_PASSWORD=$env:DB_PASSWORD"
            "SERVER_PORT=$env:SERVER_PORT"
        ) | Set-Content -Path $launchEnvPath -Encoding ascii
        Write-Output "Prepared encrypted VS Code database settings in .vscode/app.env."
        return
    }

    Write-Output "Starting Spring Boot on port $ServerPort with the PostgreSQL database '$database'."
    & $mavenWrapper spring-boot:run
    $applicationExitCode = $LASTEXITCODE
}
finally {
    Pop-Location
}

if ($null -ne $applicationExitCode) {
    exit $applicationExitCode
}
