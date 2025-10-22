@echo off
echo ========================================
echo Compilando proyecto...
echo ========================================
cd /d "%~dp0"
call mvn clean install -DskipTests
pause
@echo off
echo ========================================
echo Compilando Oreo Insight Factory
echo ========================================
echo.

cd /d "%~dp0"
call mvn clean install -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilacion exitosa!
    echo ========================================
    echo.
    echo Ejecutando la aplicacion...
    echo.
    call mvn spring-boot:run
) else (
    echo.
    echo ========================================
    echo Error en la compilacion
    echo ========================================
    pause
)

