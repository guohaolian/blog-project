param(
  [Parameter(Mandatory = $true)][int]$Port
)

$ErrorActionPreference = 'Stop'

$line = (netstat -ano | findstr ":$Port" | findstr "LISTENING" | Select-Object -First 1)
if (-not $line) {
  Write-Host "No LISTENING process found on port $Port" -ForegroundColor Yellow
  exit 0
}

$portPid = ($line -split "\s+")[-1]
Write-Host "Killing PID=$portPid on port $Port" -ForegroundColor Cyan
Stop-Process -Id $portPid -Force
Write-Host "Done" -ForegroundColor Green
