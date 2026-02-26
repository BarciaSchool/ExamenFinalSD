FROM openjdk:17-jdk-slim

# Instalar dependencias necesarias
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copiar el JAR del servidor
COPY battleship-server.jar /app/battleship-server.jar

# Exponer puerto del servidor
EXPOSE 9090

# Ejecutar el servidor con mejor configuración para producción
CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "battleship-server.jar"]
