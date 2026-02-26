#!/bin/bash
# Cliente Batalla Naval - Producción

echo "===================================="
echo "BATALLA NAVAL - CLIENTE"
echo "===================================="
echo ""

# Conectar al servidor
SERVER_HOST="localhost"
SERVER_PORT="9090"

echo "Conectando a: $SERVER_HOST:$SERVER_PORT"
echo ""

# Ejecutar cliente
java -cp target/dist/battleship-server.jar com.battleship.ui.login.LoginGUI
