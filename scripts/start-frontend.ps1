param(
  [ValidateSet('web','admin')] [string]$App = 'web'
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$dir = if ($App -eq 'admin') { Join-Path $projectRoot 'blog-admin-web' } else { Join-Path $projectRoot 'blog-web' }

Write-Host "[$App] Installing deps (if needed)..." -ForegroundColor Cyan
Push-Location $dir
try {
  npm install
  Write-Host "[$App] Starting dev server..." -ForegroundColor Cyan
  npm run dev
} finally {
  Pop-Location
}
