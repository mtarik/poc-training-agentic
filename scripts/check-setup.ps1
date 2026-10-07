# Verifie que le poste est pret pour la formation (Windows PowerShell 5.1+).
# Fichier volontairement sans accents : PowerShell 5.1 lit les scripts UTF-8 sans BOM en ANSI.

$script:allOk = $true
$root = Split-Path -Parent $PSScriptRoot

function Test-Step {
    param([string]$Label, [scriptblock]$Check)
    $result = $false
    try { $result = & $Check } catch { $result = $false }
    if ($result) {
        Write-Host "  [OK] $Label" -ForegroundColor Green
    } else {
        Write-Host "  [KO] $Label" -ForegroundColor Red
        $script:allOk = $false
    }
}

function Test-Url {
    param([string]$Url)
    try {
        Invoke-WebRequest -Uri $Url -Method Head -UseBasicParsing -TimeoutSec 10 | Out-Null
        return $true
    } catch {
        # Une reponse HTTP (meme 404) prouve que l'hote est joignable
        return $null -ne $_.Exception.Response
    }
}

Write-Host "Verification de l'environnement de formation"

Test-Step "Java >= 21" {
    $line = (cmd /c "java -version 2>&1" | Select-Object -First 1)
    if ($line -match 'version "(\d+)') { [int]$Matches[1] -ge 21 } else { $false }
}
Test-Step "Git installe" { $null -ne (Get-Command git -ErrorAction SilentlyContinue) }
Test-Step "Acces a Maven Central" { Test-Url "https://repo.maven.apache.org/maven2/" }
Test-Step "Acces a l'API Anthropic" { Test-Url "https://api.anthropic.com" }
Test-Step "Claude Code installe" { $null -ne (Get-Command claude -ErrorAction SilentlyContinue) }
Test-Step "Build et tests du projet (mvnw verify)" {
    Push-Location $root
    try {
        & "$root\mvnw.cmd" -q -B verify | Out-Null
        $LASTEXITCODE -eq 0
    } finally {
        Pop-Location
    }
}

if ($script:allOk) {
    Write-Host "Tout est pret." -ForegroundColor Green
    exit 0
} else {
    Write-Host "Certains prerequis manquent : voir formation\labs\lab-00-installation.md" -ForegroundColor Yellow
    exit 1
}
