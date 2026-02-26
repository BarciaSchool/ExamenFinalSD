#!/bin/bash
# Servidor Batalla Naval - Producción

# Configurar conexión a BD
export DB_URL="jdbc:postgresql://localhost:54322/battleship_db"
export DB_USER="postgres"
export DB_PASS="admin"

# Opción para producción (descomentar y modificar)
# export DB_URL="jdbc:postgresql://tu-servidor:5432/battleship_db"
# export DB_USER="tu_usuario"
# export DB_PASS="tu_password"

echo "===================================="
echo "BATALLA NAVAL - SERVIDOR"
echo "===================================="
echo ""
echo "Conexion BD: $DB_URL"
echo ""

# Ejecutar servidor
java -jar target/dist/battleship-server.jar
