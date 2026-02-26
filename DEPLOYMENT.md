# 🚢 BATALLA NAVAL - Guía de Producción

## 📦 Empaquetado para Producción

### 1. Compilar el proyecto
```bash
cd java
mvn clean package
```

Esto generará:
- `target/dist/battleship-server.jar` - JAR ejecutable con todas las dependencias

---

## 🚀 Ejecutar en Producción

### Windows
```cmd
# Servidor
start-server.bat

# Cliente
start-client.bat

# Monitor (Admin)
start-monitor.bat
```

### Linux/Mac
```bash
# Dar permisos primero
chmod +x start-*.sh

# Servidor
./start-server.sh

# Cliente
./start-client.sh

# Monitor (Admin)
./start-monitor.sh
```

---

## ⚙️ Configuración de Base de Datos

### Desarrollo (por defecto)
- **Host:** localhost
- **Puerto:** 54322
- **BD:** battleship_db
- **Usuario:** postgres
- **Password:** admin

### Producción

Edita los scripts `start-server.bat` o `start-server.sh`:

**Windows (.bat):**
```batch
set DB_URL=jdbc:postgresql://tu-servidor:5432/battleship_db
set DB_USER=tu_usuario
set DB_PASS=tu_password
```

**Linux (.sh):**
```bash
export DB_URL="jdbc:postgresql://tu-servidor:5432/battleship_db"
export DB_USER="tu_usuario"
export DB_PASS="tu_password"
```

---

## 🔐 Crear Administradores

### Opción 1: Directamente en PostgreSQL
```sql
-- Conéctate a la BD y ejecuta:
UPDATE jugadores SET role = 'ADMIN' WHERE username = 'tu_usuario';
```

### Opción 2: Credenciales temporales
- **Usuario:** ADMIN
- **Password:** admin123

---

## 📋 Estructura de Archivos

```
java/
├── target/
│   └── dist/
│       └── battleship-server.jar    ← JAR para producción
├── start-server.bat                   ← Iniciar servidor (Windows)
├── start-client.bat                   ← Iniciar cliente (Windows)
├── start-monitor.bat                  ← Iniciar monitor (Windows)
├── start-server.sh                    ← Iniciar servidor (Linux/Mac)
├── start-client.sh                    ← Iniciar cliente (Linux/Mac)
├── start-monitor.sh                   ← Iniciar monitor (Linux/Mac)
└── src/main/java/...
    └── com/battleship/...
```

---

## 🌐 Configuración de Red

Por defecto:
- **Puerto del servidor:** 9090
- **Host del servidor:** localhost

Para cambiar el puerto, edita:
- `java/src/main/java/com/battleship/server/ServerMain.java` (línea 12)

---

## ✅ Checklist de Producción

- [ ] Compilar: `mvn clean package`
- [ ] Configurar base de datos (editar scripts)
- [ ] Crear administrador (UPDATE en BD)
- [ ] Verificar que PostgreSQL esté corriendo
- [ ] Ejecutar el servidor: `start-server.bat`
- [ ] Probar con 2 clientes
- [ ] Probar el monitor administrativo

---

## 🐛 Troubleshooting

### Error: "No se puede conectar al servidor"
- Verifica que el servidor esté corriendo
- Verifica el puerto 9090 no esté bloqueado por el firewall

### Error: "No se pudo conectar a BD"
- Verifica que PostgreSQL esté corriendo
- Verifica las credenciales en los scripts .bat/.sh
- Verifica que la BD `battleship_db` exista

### Error: "UnsupportedClassVersionError"
- Asegúrate de usar Java 17 o superior
- Verifica con `java -version`

---

## 📊 Arquitectura

```
┌─────────────────┐
│   PostgreSQL     │
│  (battleship_db) │
└────────┬─────────┘
         │
         ▼
┌─────────────────┐      ┌──────────────┐
│  ServerMain     │◄────►│  Clientes    │
│  (Puerto 9090)  │      │  (Jugador)   │
└─────────────────┘      └──────────────┘
         ▲
         │
┌─────────────────┐
│  MonitorMain    │
│  (Admin)         │
└─────────────────┘
```

---

## 📞 Soporte

Para bugs o sugerencias, contactar al equipo de desarrollo.
