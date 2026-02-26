#!/bin/bash
echo "Iniciando Cliente de Batalla Naval..."
echo "Conectando a: akenix.asesoriasmurillo.com:9090"
echo ""
java -jar battleship.jar
if [ $? -ne 0 ]; then
    echo ""
    echo "Error: No se encontro Java. Asegurate de tener Java 17+ instalado."
fi
