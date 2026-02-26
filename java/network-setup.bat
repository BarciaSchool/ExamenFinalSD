@echo off
REM ============================================
REM BATALLA NAVAL - Configurar Red
REM ============================================

echo.
echo Este script te ayudara a configurar el juego para red local.
echo.
echo PASO 1: Encontrar la IP de este equipo
echo ----------------------------------------
ipconfig | findstr /C:"IPv4"
echo.
echo NOTA: Si eres el SERVIDOR, anota la IP que aparece (ej. 192.168.1.X)
echo        Si eres un CLIENTE, usa la IP del servidor.
echo.
echo ============================================
echo.

REM Leer IP del servidor
set /p SERVER_IP="Ingresa la IP del servidor (ej. 192.168.1.100): "

echo.
echo Configurando cliente para conectarse a: %SERVER_IP%
echo.

REM Crear archivo de configuración
echo server.host=%SERVER_IP%> config.properties
echo server.port=9090>> config.properties

echo.
echo ============================================
echo CONFIGURACION COMPLETADA
echo ============================================
echo.
echo Servidor: %SERVER_IP%:%SERVER_PORT%
echo.
echo Ahora puedes ejecutar:
echo   - Servidor: start-server.bat
echo   - Cliente: start-client.bat
echo.
pause
