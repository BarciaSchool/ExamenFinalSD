# Despliegue en Dokploy

## Arquitectura

- **Servidor y BD** → En Dokploy (contenedores Docker)
- **Clientes** → Aplicación de escritorio en PCs de los usuarios

## Archivos necesarios en `deployBD/`

1. `docker-compose.yml` - Configuración de servicios
2. `Dockerfile` - Imagen del servidor
3. `battleship-server.jar` - JAR compilado (debe copiarse desde `../java/target/dist/`)

## Pasos para desplegar en Dokploy

### 1. Compilar el JAR

```bash
cd java
mvn clean package
```

### 2. Copiar el JAR a la carpeta deployBD

```bash
# Windows
copy java\target\dist\battleship-server.jar deployBD\

# Linux/Mac
cp java/target/dist/battleship-server.jar deployBD/
```

### 3. Subir a Dokploy

**Opción A: Desde Git (Recomendado)**

1. Sube los cambios a tu repositorio Git
2. En Dokploy, crea un nuevo proyecto desde Git
3. Dokploy detectará automáticamente el `docker-compose.yml`

**Opción B: Subida manual**

1. Comprime la carpeta `deployBD/`
2. Súbela a tu servidor vía SCP/SFTP
3. Descomprímela donde Dokploy gestione los proyectos

### 4. Configurar redes en Dokploy

**IMPORTANTE**: En Dokploy **NO se exponen puertos manualmente** en el docker-compose. Dokploy maneja esto automáticamente.

- El servicio `server` escucha internamente en el puerto **9090**
- Dokploy detecta este puerto y lo expone automáticamente
- La base de datos (`db`) es interna, no accesible desde fuera

**En la UI de Dokploy:**
1. Configura el dominio/IP donde se accederá al servidor
2. Dokploy detectará automáticamente el puerto 9090 del servicio `server`
3. Los clientes se conectarán a: `tu-dominio.com:9090` (o IP:9090)

### 5. Variables de entorno (Opcional)

Puedes crear un archivo `.env` basado en `.env.example`:

```bash
cp .env.example .env
# Edita .env con tus valores
```

## Configurar clientes

Los clientes deben conectarse a la **IP pública de tu servidor Dokploy**:

### En cada PC cliente:

1. Copia estos archivos:
   - `network-setup.bat` (desde `java/`)
   - `start-client.bat` (desde `java/`)
   - `target/dist/battleship-server.jar` (desde `java/`)

2. Ejecuta `network-setup.bat`:

   ```
   Ingresa la IP del servidor: [TU_IP_PUBLICA_O_DOMINIO]
   ```

3. Ejecuta `start-client.bat`

### Usar dominio (opcional)

Si tienes un dominio, puedes configurarlo en lugar de IP:

```
server.host=tu-dominio.com
server.port=9090
```

## Verificar despliegue

### En Dokploy:

- Verifica que ambos contenedores estén "healthy"
- Revisa los logs del servidor
- Monitorea el consumo de recursos

### Desde tu PC:

```bash
# Verificar que el puerto esté accesible
telnet tu-ip-servidor 9090
```

## Crear administrador

```bash
# Accede a la BD a través de Dokploy o docker exec
docker exec -it battleship_db psql -U postgres -d battleship_db

# Ejecutar
UPDATE players SET role = 'ADMIN' WHERE username = 'tu_usuario';
\q
```

## Solución de problemas

### Contenedor "server" no inicia

```bash
# Ver logs
docker-compose logs -f server

# Entrar al contenedor para debug
docker exec -it battleship_server sh
```

### Los clientes no pueden conectarse

1. **Verifica el puerto 9090**:
   ```bash
   # En el servidor, verificar que esté escuchando
   netstat -tlnp | grep 9090
   ```

2. **Firewall del servidor**:
   ```bash
   # Si usas ufw
   sudo ufw allow 9090/tcp

   # Si usas firewalld
   sudo firewall-cmd --add-port=9090/tcp --permanent
   sudo firewall-cmd --reload
   ```

3. **Configuración de Dokploy**: Asegúrate de que el puerto 9090 esté mapeado correctamente

### La BD no está lista

El docker-compose usa `healthcheck` para esperar a que la BD esté lista. Si aún así hay problemas:

```bash
# Aumentar el tiempo de espera en Dockerfile
# Agregar antes de CMD: sleep 15
```

## Actualizar la aplicación

Cuando hagas cambios:

```bash
# 1. Compilar nuevo JAR
mvn clean package

# 2. Copiar a deployBD
cp java/target/dist/battleship-server.jar deployBD/

# 3. En Dokploy:
#    - Hacer commit/git push si usas Git, o
#    - Subir el archivo nuevo manualmente

# 4. Redesplegar
docker-compose up -d --build server
```
