param(
  [int]$Port = 18080
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$apiDir = Join-Path $projectRoot 'blog-api'
$jarPath = Join-Path $apiDir 'target\blog-api-0.0.1-SNAPSHOT.jar'

Write-Host "[blog-api] Building..." -ForegroundColor Cyan
Push-Location $apiDir
try {
  mvn -q -DskipTests package
} finally {
  Pop-Location
}

if (-not (Test-Path $jarPath)) {
  throw "Jar not found: $jarPath"
}

Write-Host "[blog-api] Starting on port $Port (profile=local)..." -ForegroundColor Cyan
Write-Host "[blog-api] Health check: http://127.0.0.1:$Port/api/health" -ForegroundColor Gray

Start-Process -FilePath "java" -ArgumentList @(
  "-jar",
  $jarPath,
  "--spring.profiles.active=local",
  "--server.port=$Port"
) -NoNewWindow

Write-Host "[blog-api] Started (background). Use stop-port.ps1 to stop." -ForegroundColor Green
