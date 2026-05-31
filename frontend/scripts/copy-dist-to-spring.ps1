$ErrorActionPreference = "Stop"

$frontendRoot = Resolve-Path -LiteralPath (Join-Path $PSScriptRoot "..")
$repoRoot = Resolve-Path -LiteralPath (Join-Path $frontendRoot "..")
$dist = Join-Path $frontendRoot "dist"
$static = Join-Path $repoRoot "backend\src\main\resources\static"

if (-not (Test-Path -LiteralPath $dist)) {
    throw "Frontend dist folder not found. Run npm run build first."
}

New-Item -ItemType Directory -Force -Path $static | Out-Null
Get-ChildItem -LiteralPath $static -Force | Where-Object { $_.Name -ne ".gitkeep" } | Remove-Item -Recurse -Force
Get-ChildItem -LiteralPath $dist -Force | Copy-Item -Destination $static -Recurse -Force

Write-Host "Copied frontend/dist to backend/src/main/resources/static"
