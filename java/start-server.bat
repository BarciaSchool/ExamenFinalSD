@echo off
REM Servidor Batalla Naval - Producción en Red

echo ====================================
echo BATALLA NAVAL - SERVIDOR
echo ====================================
echo.

MOSTRAR IP:
ipconfig | findstr /C:"IPv4"
echo.

echo Iniciando servidor en puerto 9090...
echo Este equipo sera el SERVIDOR.
echo Los clientes deben conectarse a tu IP.
echo.

REM Ejecutar servidor
java -jar target/dist/battleship-server.jar

pause
