$ErrorActionPreference = "Stop"

$checks = @()

function Add-Check {
    param(
        [string]$Name,
        [bool]$Passed,
        [string]$Details
    )

    $script:checks += [PSCustomObject]@{
        Check = $Name
        Result = if ($Passed) { "PASS" } else { "FAIL" }
        Details = $Details
    }
}

Write-Host "`n=== AUDITORIA DEL REPOSITORIO ===" -ForegroundColor Cyan

Add-Check "Dockerfile" `
    (Test-Path "Dockerfile") `
    "Archivo de construccion de imagen"

Add-Check "Docker Compose" `
    (Test-Path "compose.yaml") `
    "Configuracion de servicios locales"

Add-Check "Workflow CI" `
    (Test-Path ".github/workflows/ci.yml") `
    "Pipeline de integracion continua"

Add-Check "Kubernetes" `
    (Test-Path "k8s/03-app.yaml") `
    "Manifiesto de la aplicacion"

$jacocoConfigured = $false
if (Test-Path "pom.xml") {
    $jacocoConfigured = Select-String `
        -Path "pom.xml" `
        -Pattern '<minimum>0\.80</minimum>' `
        -Quiet
}

Add-Check "JaCoCo 80%" `
    $jacocoConfigured `
    "Umbral minimo de cobertura configurado"

Add-Check "CODEOWNERS" `
    ((Test-Path ".github/CODEOWNERS") -or (Test-Path "CODEOWNERS")) `
    "Responsables de revision"

Add-Check "Snyk" `
    (Test-Path ".snyk") `
    "Configuracion de seguridad"

Write-Host ""
$checks | Format-Table -AutoSize

$passed = @($checks | Where-Object { $_.Result -eq "PASS" }).Count
$failed = @($checks | Where-Object { $_.Result -eq "FAIL" }).Count

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Correctos: $passed" -ForegroundColor Green
Write-Host "Fallidos:  $failed" -ForegroundColor Red
Write-Host "==================================" -ForegroundColor Cyan

if ($failed -gt 0) {
    Write-Host "Auditoria finalizada con observaciones." -ForegroundColor Yellow
    exit 1
}

Write-Host "Auditoria completada correctamente." -ForegroundColor Green
exit 0
