#!/bin/bash
# Monitor Batalla Naval - Producción

echo "===================================="
echo "BATALLA NAVAL - MONITOR ADMIN"
echo "===================================="
echo ""

# Conectar al servidor
SERVER_HOST="localhost"
SERVER_PORT="9090"

echo "Conectando a: $SERVER_HOST:$SERVER_PORT"
echo ""

# Ejecutar monitor
java -cp target/dist/battleship-server.jar com.battleship.monitor.MonitorMain
