FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiar el JAR del servidor
COPY battleship-server.jar /app/battleship-server.jar

# Ejecutar el servidor con mejor configuración para producción
CMD ["java", "-Xms256m", "-Xmx512m", "-jar", "battleship-server.jar"]
