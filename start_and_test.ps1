# Script para arrancar el servidor y probar todos los endpoints del Checkpoint 1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "CHECKPOINT 1 - AUTENTICACION JWT" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Paso 1: Matar cualquier proceso Java que pueda estar corriendo
Write-Host "[1/6] Limpiando procesos Java previos..." -ForegroundColor Yellow
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2

# Paso 2: Arrancar el servidor
Write-Host "[2/6] Arrancando el servidor en puerto 8081..." -ForegroundColor Yellow
$serverProcess = Start-Process java -ArgumentList '"-Djwt.secret=DevSecretKeyForOreo1234567890123456"', '-jar', 'target\insight-factory-1.0.0.jar' -PassThru -WindowStyle Hidden
Write-Host "Servidor iniciado con PID: $($serverProcess.Id)" -ForegroundColor Green

# Paso 3: Esperar a que el servidor arranque
Write-Host "[3/6] Esperando 15 segundos a que el servidor arranque..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

# Paso 4: Probar endpoint de registro CENTRAL
Write-Host "[4/6] Probando registro de usuario CENTRAL..." -ForegroundColor Yellow
try {
    $registerCentralBody = @{
        username = "oreo.admin"
        email = "admin@oreo.com"
        password = "Oreo1234"
        role = "CENTRAL"
    } | ConvertTo-Json

    $registerCentralResponse = Invoke-RestMethod -Uri "http://localhost:8081/auth/register" -Method POST -Body $registerCentralBody -ContentType "application/json"
    Write-Host "✓ Registro CENTRAL exitoso!" -ForegroundColor Green
    Write-Host "  Usuario ID: $($registerCentralResponse.id)" -ForegroundColor Gray
    Write-Host "  Username: $($registerCentralResponse.username)" -ForegroundColor Gray
    Write-Host "  Role: $($registerCentralResponse.role)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Error en registro CENTRAL: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Paso 5: Probar endpoint de registro BRANCH
Write-Host "[5/6] Probando registro de usuario BRANCH (Miraflores)..." -ForegroundColor Yellow
try {
    $registerBranchBody = @{
        username = "miraflores.user"
        email = "mira@oreo.com"
        password = "Oreo1234"
        role = "BRANCH"
        branch = "Miraflores"
    } | ConvertTo-Json

    $registerBranchResponse = Invoke-RestMethod -Uri "http://localhost:8081/auth/register" -Method POST -Body $registerBranchBody -ContentType "application/json"
    Write-Host "✓ Registro BRANCH exitoso!" -ForegroundColor Green
    Write-Host "  Usuario ID: $($registerBranchResponse.id)" -ForegroundColor Gray
    Write-Host "  Username: $($registerBranchResponse.username)" -ForegroundColor Gray
    Write-Host "  Role: $($registerBranchResponse.role)" -ForegroundColor Gray
    Write-Host "  Branch: $($registerBranchResponse.branch)" -ForegroundColor Gray
} catch {
    Write-Host "✗ Error en registro BRANCH: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Paso 6: Probar login CENTRAL y guardar token
Write-Host "[6/6] Probando login de usuario CENTRAL..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = "oreo.admin"
        password = "Oreo1234"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/auth/login" -Method POST -Body $loginBody -ContentType "application/json"
    Write-Host "✓ Login CENTRAL exitoso!" -ForegroundColor Green
    Write-Host "  Role: $($loginResponse.role)" -ForegroundColor Gray
    Write-Host "  Expires in: $($loginResponse.expiresIn) segundos" -ForegroundColor Gray
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "TOKEN JWT GENERADO:" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host $loginResponse.token -ForegroundColor Yellow
    Write-Host ""

    # Guardar token en archivo para fácil acceso
    $loginResponse.token | Out-File -FilePath "token.txt" -Encoding UTF8
    Write-Host "✓ Token guardado en token.txt" -ForegroundColor Green
} catch {
    Write-Host "✗ Error en login CENTRAL: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Resumen final
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "CHECKPOINT 1 COMPLETADO" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "El servidor está corriendo en: http://localhost:8081" -ForegroundColor White
Write-Host "Consola H2: http://localhost:8081/h2-console" -ForegroundColor White
Write-Host ""
Write-Host "Usuarios creados:" -ForegroundColor White
Write-Host "  1. oreo.admin (CENTRAL) - Acceso completo" -ForegroundColor Gray
Write-Host "  2. miraflores.user (BRANCH - Miraflores) - Acceso limitado" -ForegroundColor Gray
Write-Host ""
Write-Host "Token JWT guardado en: token.txt" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SIGUIENTE PASO: CONFIGURAR POSTMAN" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Lee el archivo POSTMAN_GUIDE.md para las instrucciones completas." -ForegroundColor White
Write-Host ""
Write-Host "Presiona CTRL+C para detener el servidor cuando termines." -ForegroundColor Yellow
Write-Host ""

# Mantener el servidor corriendo
Write-Host "Servidor corriendo... (PID: $($serverProcess.Id))" -ForegroundColor Green
Wait-Process -Id $serverProcess.Id

