# Prepare le depot pour la demo : installe le CLAUDE.md et l'outillage .claude/ de reference.
$ErrorActionPreference = 'Stop'
$root = Resolve-Path (Join-Path $PSScriptRoot '..\..')
Set-Location $root
$sol = Join-Path $root 'formation\solutions'

if (Test-Path 'src\main\java\fr\formation\shop\domain\review') {
    Write-Host "ATTENTION : le contexte review existe deja (demo deja jouee ?). Remettez le depot a zero avec git avant la demo." -ForegroundColor Red
    exit 1
}

Write-Host "Copie de CLAUDE.md et de .claude/ (skills, agents, hooks, settings)..."
Copy-Item (Join-Path $sol 'CLAUDE.md') 'CLAUDE.md' -Force
New-Item -ItemType Directory -Force '.claude' | Out-Null
Copy-Item (Join-Path $sol '.claude\*') '.claude' -Recurse -Force

Write-Host "Prechauffage du cache Maven et verification du build..."
& "$root\mvnw.cmd" -q -B verify
if ($LASTEXITCODE -ne 0) { Write-Host "Le build echoue : corrigez avant la demo." -ForegroundColor Red; exit 1 }

Write-Host ""
Write-Host "Pret. Conseille : git add -A ; git commit -m 'Avant demo SHOP-201'" -ForegroundColor Green
Write-Host "Puis : claude   (et suivez formation\demo\README.md)"
