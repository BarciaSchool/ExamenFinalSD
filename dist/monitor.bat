@echo off
title Batalla Naval - Monitor de Administracion
echo Iniciando Monitor de Administracion...
echo Conectando a: akenix.asesoriasmurillo.com:9090
echo.
echo Credenciales:
echo   Usuario: ADMIN
echo   Contrasena: admin123
echo.
java -jar battleship.jar --monitor
if errorlevel 1 (
    echo.
    echo Error: No se encontro Java. Asegurate de tener Java 17+ instalado.
    pause
)
