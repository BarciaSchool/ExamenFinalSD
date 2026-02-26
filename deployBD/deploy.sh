#!/bin/bash

echo "===================================="
echo "BATALLA NAVAL - DESPLIEGUE LINUX"
echo "===================================="
echo ""

# Verificar que existe el JAR
if [ ! -f "../java/target/dist/battleship-server.jar" ]; then
    echo "ERROR: No existe battleship-server.jar"
    echo "Primero ejecuta: mvn clean package"
    exit 1
fi

echo "JAR encontrado. Iniciando servicios..."
echo ""

# Construir y levantar contenedores
docker-compose down 2>/dev/null  # Detener si están corriendo
docker-compose build --no-cache
docker-compose up -d

echo ""
echo "===================================="
echo "SERVICIOS INICIADOS"
echo "===================================="
echo ""
echo "Base de datos: puerto 54322"
echo "Servidor: puerto 9090"
echo ""
echo "Ver logs con: docker-compose logs -f"
echo "Detener con: docker-compose down"
echo ""
