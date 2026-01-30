param(
  [int]$ApiPort = 18080
)

$ErrorActionPreference = 'Stop'

Write-Host "Checking blog-api health..." -ForegroundColor Cyan
cmd /c ("curl -s -i http://127.0.0.1:{0}/api/health" -f $ApiPort)
