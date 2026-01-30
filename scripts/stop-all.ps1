param(
  [int]$ApiPort = 18080,
  [int]$WebPort = 5173,
  [int]$AdminPort = 5174
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$stopPort = Join-Path $projectRoot 'scripts\stop-port.ps1'
$pidsFile = Join-Path $PSScriptRoot '.pids.json'

function Stop-ByPid([int]$ProcessId) {
  if (-not $ProcessId) { return }
  $proc = Get-Process -Id $ProcessId -ErrorAction SilentlyContinue
  if (-not $proc) {
    Write-Host "PID $ProcessId not running" -ForegroundColor Yellow
    return
  }
  Write-Host "Stopping PID=$ProcessId ($($proc.ProcessName))" -ForegroundColor Cyan
  Stop-Process -Id $ProcessId -Force
}

if (Test-Path $pidsFile) {
  Write-Host "Found $pidsFile, stopping by PID..." -ForegroundColor Cyan
  $pids = Get-Content $pidsFile -Raw | ConvertFrom-Json

  # Stop child PowerShell windows first (they host npm dev servers)
  Stop-ByPid $pids.webPid
  Stop-ByPid $pids.adminPid

  # Stop api java process (tracked by port PID at start time)
  Stop-ByPid $pids.apiPid

  Remove-Item $pidsFile -Force -ErrorAction SilentlyContinue
  Write-Host "Stopped." -ForegroundColor Green
  exit 0
}

Write-Host "No .pids.json found. Falling back to stop by port." -ForegroundColor Yellow
Write-Host "Stopping ports: api=$ApiPort web=$WebPort admin=$AdminPort" -ForegroundColor Cyan

& $stopPort -Port $ApiPort
& $stopPort -Port $WebPort
& $stopPort -Port $AdminPort

Write-Host "Stopped." -ForegroundColor Green
