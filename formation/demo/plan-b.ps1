# Plan B : installe l'implementation de reference de SHOP-201 si la demo live deraille.
$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..\..')
Set-Location $root

Write-Host "Copie de l'implementation de reference SHOP-201..."
Copy-Item 'formation\solutions\demo-shop-201\src\*' 'src' -Recurse -Force

& "$root\mvnw.cmd" -q -B verify
if ($LASTEXITCODE -eq 0) { Write-Host "SHOP-201 installe, build vert. Lancez : mvnw.cmd spring-boot:run" -ForegroundColor Green }
