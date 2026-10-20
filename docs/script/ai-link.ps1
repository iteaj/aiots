param($Tool)

$ROOT = Split-Path -Parent (Split-Path -Parent $PSScriptRoot)
$AI_DIR = Join-Path $ROOT "docs\ai"

$tools = @{
  qoder  = ".qoder"
  cursor = ".cursor"
  trae   = ".trae"
  claude = ".claude"
  codex  = ".codex"
}

if (-not $Tool) {
  Write-Host "用法: .\ai-link.ps1 <工具类型>"
  Write-Host "工具类型支持: qoder cursor trae claude codex"
  exit 1
}

$dir = $tools[$Tool.ToLower()]
if (-not $dir) {
  Write-Host "不支持的工具: $Tool"
  Write-Host "工具类型支持: qoder cursor trae claude codex"
  exit 1
}

$linkPath = Join-Path $ROOT $dir
if (Test-Path $linkPath) {
  Write-Host "[跳过] $dir 已存在"
} else {
  New-Item -ItemType Junction -Path $linkPath -Target $AI_DIR > $null
  Write-Host "[创建] $dir -> docs/ai"
}
