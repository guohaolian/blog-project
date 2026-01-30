param(
  [int]$ApiPort = 18080,
  [switch]$NoFrontend
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$pidsFile = Join-Path $PSScriptRoot '.pids.json'

$pids = [ordered]@{
  apiPort = $ApiPort
  startedAt = (Get-Date).ToString('s')
  apiPid = $null
  webPid = $null
  adminPid = $null
}

Write-Host "== Start blog-api (local) ==" -ForegroundColor Cyan
# start-backend-local.ps1 will build + start the jar.
& (Join-Path $projectRoot 'scripts\start-backend-local.ps1') -Port $ApiPort

# Find PID by port (best-effort). If api fails to bind, this stays null.
$line = (netstat -ano | findstr ":$ApiPort" | findstr "LISTENING" | Select-Object -First 1)
if ($line) {
  $pids.apiPid = ($line -split "\s+")[-1]
}

if (-not $NoFrontend) {
  Write-Host "== Start blog-web ==" -ForegroundColor Cyan
  $webProc = Start-Process -FilePath "powershell" -ArgumentList @(
    "-NoProfile",
    "-ExecutionPolicy","Bypass",
    "-File", (Join-Path $projectRoot 'scripts\start-frontend.ps1'),
    "-App","web"
  ) -PassThru
  $pids.webPid = $webProc.Id

  Write-Host "== Start blog-admin-web ==" -ForegroundColor Cyan
  $adminProc = Start-Process -FilePath "powershell" -ArgumentList @(
    "-NoProfile",
    "-ExecutionPolicy","Bypass",
    "-File", (Join-Path $projectRoot 'scripts\start-frontend.ps1'),
    "-App","admin"
  ) -PassThru
  $pids.adminPid = $adminProc.Id
}

$pids | ConvertTo-Json | Set-Content -Encoding UTF8 $pidsFile

Write-Host "All started (api + 2 frontends)." -ForegroundColor Green
Write-Host "- blog-web:        http://localhost:5173" -ForegroundColor Gray
Write-Host "- blog-admin-web:  http://localhost:5174" -ForegroundColor Gray
Write-Host "- blog-api health: http://127.0.0.1:$ApiPort/api/health" -ForegroundColor Gray
Write-Host "PIDs saved to: $pidsFile" -ForegroundColor Gray
