@echo off
REM Cliente Batalla Naval - Red Local
REM Lee la configuración de config.properties

echo ====================================
echo BATALLA NAVAL - CLIENTE
echo ====================================
echo.

REM Verificar que exista config.properties
if not exist config.properties (
    echo ERROR: No existe config.properties
    echo Ejecuta primero: network-setup.bat
    pause
    exit /b 1
)

REM Leer configuración
for /f "tokens=1,2 delims==" %%a in (config.properties) do (
    if "%%a"=="server.host" set SERVER_HOST=%%b
    if "%%a"=="server.port" set SERVER_PORT=%%b
)

echo Configuracion leida:
echo   Servidor: %SERVER_HOST%:%SERVER_PORT%
echo.

REM Ejecutar cliente
java -cp target/dist/battleship-server.jar com.battleship.ui.login.LoginGUI

pause
