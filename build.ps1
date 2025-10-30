# PowerShell build script: compile sources and create runnable JAR including resources
Set-StrictMode -Version Latest

#$ErrorActionPreference = 'Stop'
$bin = ".\bin"
if (-not (Test-Path $bin)) { New-Item -ItemType Directory -Path $bin | Out-Null }

# Collect sources into sources.txt
Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName } > sources.txt

# Compile
javac -d .\bin @sources.txt
if ($LASTEXITCODE -ne 0) { Write-Error "Compilation failed (javac exit code $LASTEXITCODE)"; exit $LASTEXITCODE }

# Create manifest (ensure newline at end)
Set-Content -Path manifest.txt -Value 'Main-Class: main.Main'
Add-Content -Path manifest.txt -Value ''

# Create jar (include compiled classes and resources)
jar cfm SlimeVengeance.jar manifest.txt -C .\bin . -C .\src\res .
if ($LASTEXITCODE -ne 0) { Write-Error "Jar creation failed (jar exit code $LASTEXITCODE)"; exit $LASTEXITCODE }

Write-Host "Created SlimeVengeance.jar" -ForegroundColor Green
