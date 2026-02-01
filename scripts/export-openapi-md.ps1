Param(
  [string]$ApiBase = "http://localhost:8080",
  [string]$SpecPath = "docs/generated/openapi.json"
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

# 2) 生成 Markdown（widdershins）
# 说明：widdershins 输出偏“接口参考手册风格”，不如 Redoc 好看，但适合提交到仓库 diff。
$mdOut = Join-Path $outDir "api-docs.md"
Write-Host "Generating Markdown via widdershins -> $mdOut"

Push-Location $root
try {
  # --yes: 自动确认安装
  npx --yes widdershins $specAbs -o $mdOut --language_tabs 'shell:Shell' 'javascript:JavaScript' --omitHeader
} finally {
  Pop-Location
}

Write-Host "Markdown generated: $mdOut" -ForegroundColor Green
