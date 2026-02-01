Param(
  [string]$ApiBase = "http://localhost:8080",
  [string]$SpecPath = "docs/generated/openapi.json",
  [switch]$SplitByGroup = $true
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$outDir = Join-Path $root "docs\generated"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

# 1) 确保 spec 已导出
$specAbs = Join-Path $root $SpecPath
if (-not (Test-Path $specAbs)) {
  Write-Host "OpenAPI spec not found: $specAbs. Exporting first..." -ForegroundColor Yellow
  & (Join-Path $root "scripts\export-openapi.ps1") -ApiBase $ApiBase
}

function Build-Redoc([string]$specFile, [string]$outFile, [string]$title) {
  Write-Host "Generating HTML via redoc-cli -> $outFile"
  npx --yes redoc-cli bundle $specFile -o $outFile --title $title
}

Push-Location $root
try {
  if ($SplitByGroup) {
    $webSpec = Join-Path $outDir "openapi.web.json"
    $adminSpec = Join-Path $outDir "openapi.admin.json"

    $builtAny = $false

    if (Test-Path $webSpec) {
      $outWeb = Join-Path $outDir "api-docs.web.html"
      Build-Redoc $webSpec $outWeb "Blog Web API Docs"
      $builtAny = $true
    }

    if (Test-Path $adminSpec) {
      $outAdmin = Join-Path $outDir "api-docs.admin.html"
      Build-Redoc $adminSpec $outAdmin "Blog Admin API Docs"
      $builtAny = $true
    }

    if ($builtAny) {
      Write-Host "Grouped HTML generated under: $outDir" -ForegroundColor Green
      return
    }

    Write-Host "Group specs not found. Falling back to single HTML (openapi.json)." -ForegroundColor Yellow
  }

  $redocOut = Join-Path $outDir "api-docs.html"
  Build-Redoc $specAbs $redocOut "Blog API Docs"
  Write-Host "HTML generated: $redocOut" -ForegroundColor Green
}
finally {
  Pop-Location
}
