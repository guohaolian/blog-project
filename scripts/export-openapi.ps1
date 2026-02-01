Param(
  [string]$ApiBase = "http://localhost:8080",
  [string]$JarPath = "blog-api/target/blog-api-0.0.1-SNAPSHOT-exec.jar",
  [int]$StartPort = 18080
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$outDir = Join-Path $root "docs\generated"
New-Item -ItemType Directory -Force -Path $outDir | Out-Null

function Download-Text([string]$url, [string]$path, [int]$timeoutSec = 60) {
  Write-Host "Downloading: $url -> $path"
  Invoke-WebRequest -UseBasicParsing -Uri $url -TimeoutSec $timeoutSec -OutFile $path
}

function TryDownload([string]$url, [string]$path, [int]$timeoutSec = 60) {
  try {
    Download-Text $url $path $timeoutSec
    return $true
  } catch {
    Write-Host "Skip (download failed): $url" -ForegroundColor Yellow
    return $false
  }
}

function Test-ApiUp([string]$base) {
  try {
    $r = Invoke-WebRequest -UseBasicParsing -Uri "$base/v3/api-docs" -TimeoutSec 5
    return $r.StatusCode -eq 200
  } catch {
    return $false
  }
}

function Get-FreePort([int]$startPort) {
  for ($p = $startPort; $p -lt ($startPort + 100); $p++) {
    $used = Get-NetTCPConnection -State Listen -LocalPort $p -ErrorAction SilentlyContinue
    if (-not $used) { return $p }
  }
  throw "No free port found in range [$startPort, $($startPort+99)]"
}

$proc = $null
try {
  if (-not (Test-ApiUp $ApiBase)) {
    $jarAbs = Join-Path $root $JarPath
    if (-not (Test-Path $jarAbs)) {
      throw "API not reachable ($ApiBase) and jar not found: $jarAbs. Please build blog-api first."
    }

    $port = Get-FreePort $StartPort
    $ApiBase = "http://localhost:$port"

    Write-Host "API not reachable. Starting temporary server at $ApiBase from: $jarAbs" -ForegroundColor Yellow
    $proc = Start-Process -FilePath "java" -ArgumentList "-jar",$jarAbs,"--server.port=$port" -PassThru -WindowStyle Hidden

    $ok = $false
    for ($i=0; $i -lt 40; $i++) {
      Start-Sleep -Seconds 1
      if (Test-ApiUp $ApiBase) { $ok = $true; break }
    }
    if (-not $ok) {
      throw "Started jar but API still not reachable: $ApiBase"
    }
  }

  # JSON 作为必选产物：失败就应当终止
  Download-Text "$ApiBase/v3/api-docs" (Join-Path $outDir "openapi.json") 60

  # YAML 与分组在当前项目应当可用：默认尝试下载；若环境异常则跳过
  TryDownload "$ApiBase/v3/api-docs.yaml" (Join-Path $outDir "openapi.yaml") 60 | Out-Null
  TryDownload "$ApiBase/v3/api-docs/web" (Join-Path $outDir "openapi.web.json") 60 | Out-Null
  TryDownload "$ApiBase/v3/api-docs/admin" (Join-Path $outDir "openapi.admin.json") 60 | Out-Null

  Write-Host "Export done: $outDir" -ForegroundColor Green
}
finally {
  if ($null -ne $proc) {
    Stop-Process -Id $proc.Id -Force -ErrorAction SilentlyContinue
  }
}
