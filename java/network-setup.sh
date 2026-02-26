#!/bin/bash
# ============================================
# BATALLA NAVAL - Configurar Red
# ============================================

echo ""
echo "PASO 1: Encontrar la IP de este equipo"
echo "----------------------------------------"

# Detectar OS y mostrar IP
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    IP=$(ipconfig getifaddr en0 | grep "inet " | awk '{print $2}')
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    IP=$(hostname -I | awk '{print $1}')
else
    IP="127.0.0.1"
fi

echo "Tu IP detectada es: $IP"
echo ""
echo "NOTA: Si eres el SERVIDOR, esta es tu IP."
echo "      Si eres un CLIENTE, necesitas la IP del servidor."
echo ""

# Leer IP del servidor
read -p "Ingresa la IP del servidor (ej. 192.168.1.100): " SERVER_IP

echo ""
echo "Configurando cliente para conectarse a: $SERVER_IP"
echo ""

# Crear archivo de configuración
cat > config.properties <<EOF
server.host=$SERVER_IP
server.port=9090
EOF

echo ""
echo "============================================"
echo "CONFIGURACIÓN COMPLETADA"
echo "============================================"
echo ""
echo "Servidor: $SERVER_IP:9090"
echo ""
echo "Ahora puedes ejecutar:"
echo "  - Servidor: ./start-server.sh"
echo "  - Cliente: ./start-client.sh"
echo ""
