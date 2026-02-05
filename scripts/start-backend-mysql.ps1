param(
  [ValidateSet('dev','prod')]
  [string]$Profile = 'dev',

  [int]$Port = 8080,

  # Optional: Override DB connection without editing yml
  [string]$DbHost = '',
  [int]$DbPort = 3306,
  [string]$DbName = '',
  [string]$DbUser = '',
  [string]$DbPassword = ''
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$apiDir = Join-Path $projectRoot 'blog-api'
$jarPath = Join-Path $apiDir 'target\blog-api-0.0.1-SNAPSHOT-exec.jar'

Write-Host "[blog-api] Building (skipTests)..." -ForegroundColor Cyan
Push-Location $apiDir
try {
  mvn -q -DskipTests package
} finally {
  Pop-Location
}

if (-not (Test-Path $jarPath)) {
  throw "Jar not found: $jarPath"
}

$javaArgs = @(
  "-jar",
  $jarPath,
  "--spring.profiles.active=$Profile",
  "--server.port=$Port"
)

# Allow runtime overrides (useful for server / different local DB)
if ($DbHost -ne '') {
  if ($DbName -eq '') { $DbName = 'blog_db' }
  $jdbc = "jdbc:mysql://$DbHost`:$DbPort/$DbName?useUnicode=true&characterEncoding=utf8&useSSL=false&allowMultiQueries=true"
  $javaArgs += "--spring.datasource.url=$jdbc"
}
if ($DbUser -ne '') { $javaArgs += "--spring.datasource.username=$DbUser" }
if ($DbPassword -ne '') { $javaArgs += "--spring.datasource.password=$DbPassword" }

Write-Host "[blog-api] Starting on port $Port (profile=$Profile)..." -ForegroundColor Cyan
Write-Host "[blog-api] Health check: http://127.0.0.1:$Port/api/health" -ForegroundColor Gray

Start-Process -FilePath "java" -WorkingDirectory $apiDir -ArgumentList $javaArgs -NoNewWindow

Write-Host "[blog-api] Started (background)." -ForegroundColor Green
Write-Host "[blog-api] Tip: stop with scripts/stop-port.ps1 -Port $Port" -ForegroundColor Gray
