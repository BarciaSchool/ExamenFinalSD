# Proyecto Batalla Naval - AKENIX

## Descripción del Proyecto
Juego de Batalla Naval desarrollado en Java con arquitectura cliente-servidor, que incluye monitoreo de salas, gestión de usuarios y persistencia de datos en PostgreSQL.

## Arquitectura del Proyecto

### Estructura de Paquetes
- `com.battleship.client`: Lógica del cliente y controladores
- `com.battleship.server`: Servidor y manejo de conexiones
- `com.battleship.ui`: Interfaces gráficas (login, lobby, monitoreo, juego)
  - `com.battleship.ui.login`: Interfaces de autenticación
  - `com.battleship.ui.lobby`: Interfaces de lobby y salas de espera
  - `com.battleship.ui.game`: Interfaces de juego y colocación de barcos
  - `com.battleship.ui.monitor`: Interfaces de monitoreo
- `com.battleship.game`: Gestión de salas y lógica del juego
- `com.battleship.persistence`: Acceso a datos y repositorios
- `com.battleship.model`: Modelos de datos (Player, GameRoom, Ship)
- `com.battleship.security`: Utilidades de seguridad y encriptación
- `com.battleship.validation`: Validación de entrada de datos
- `com.battleship.common`: Protocolos y clases compartidas
- `com.battleship.monitor`: Componentes de monitoreo

### Componentes Principales

#### 1. Sistema de Autenticación
- **LoginGUI**: Interfaz de inicio de sesión con validación
- **RegisterGUI**: Formulario de registro con validación en tiempo real
- **PasswordUtil**: Encriptación de contraseñas
- **InputValidator**: Validación de datos de entrada

#### 2. Gestión de Salas
- **RoomManager**: Administración de salas de juego
- **GameRoom**: Modelo de sala individual con lógica de juego
- **LobbyGUI**: Interfaz del lobby para listar y unir salas
- **RoomWaitingGUI**: Interfaz de sala de espera con 2 jugadores
  - Muestra Player1 y Player2 con estados
  - Botón para abandonar sala
  - Transición automática a ShipPlacementGUI cuando ambos conectados

#### 3. Colocación de Barcos
- **ShipPlacementGUI**: Interfaz para colocar 5 barcos en tablero 16x16
  - Sistema clic-clic (seleccionar barco → clic en tablero)
  - Tecla R para rotar barcos (horizontal ↔ vertical)
  - Hover preview (verde = válido, rojo = inválido)
  - Clic en barco colocado para eliminar y reposicionar
  - Validación de límites y solapamiento en tiempo real
  - Botón "¡LISTO!" para enviar al servidor
- **Ship**: Modelo de barco con tamaño, posición, orientación, estado

#### 4. Monitoreo
- **MonitorGUI**: Panel de monitoreo administrativo
  - Tabla en tiempo real con todas las salas
  - Estadísticas de juego (tiros, aciertos, barcos hundidos)
  - Actualización automática cuando hay cambios
- **MonitorClient**: Cliente especial para monitoreo
- **MonitorLoginGUI**: Acceso exclusivo para administradores

#### 5. Persistencia
- **PlayerDAO**: Acceso a datos de jugadores
- **PlayerRepository**: Repositorio de jugadores con PostgreSQL

#### 6. Comunicación
- **Protocol**: Definición de protocolos de comunicación
  - **Cliente → Servidor**: LOGIN, REGISTER, LOGOUT, CREATE_ROOM, JOIN_ROOM, GET_ROOMS, PLACE_SHIPS, SHOOT
  - **Servidor → Cliente**: LOGIN_OK, REGISTER_OK, LOGOUT_OK, ROOM_LIST, ROOM_CREATED, AUTO_JOINED, ROOM_INFO, JOINED_OK, PLAYER_JOINED, SHIPS_PLACED_OK, GAME_START, YOUR_TURN, OPPONENT_TURN, SHOT_RESULT, GAME_OVER, ERROR
- **ClientController**: Controlador de cliente (Singleton)
  - Gestiona conexión TCP/IP con servidor
  - Procesa mensajes del servidor
  - Maneja transiciones entre GUIs
  - Almacena estado del cliente (playerName, currentRoomId, victorias, derrotas)
- **ClientHandler**: Manejo de clientes en servidor
  - Hilo por cliente (conexión simultánea)
  - Procesa comandos según Protocol
  - Comunica con RoomManager y GameRoom

## Reglas del Juego - Batalla Naval

### Configuración del Tablero
- **Tamaño del tablero**: 16x16 cuadros (grids)
- **Jugadores**: 2 usuarios por sala
- **Barcos**: 5 barcos por jugador, con diferentes tamaños (2-5 cuadros)
- **Orientación**: Los barcos se colocan vertical u horizontalmente

### Tamaños de los Barcos
1. Barco de 2 cuadros
2. Barco de 3 cuadros
3. Barco de 4 cuadros
4. Barco de 5 cuadros
5. Barco de 5 cuadros

### Flujo de Juego

#### Fase 1: Colocación de Barcos
1. Cuando 2 usuarios se unen a una sala, ambos acceden a la interfaz de colocación de barcos
2. Cada usuario ve su propio tablero 16x16 vacío
3. Cada usuario debe colocar estratégicamente sus 5 barcos en el tablero
4. La colocación es individual y privada (cada usuario ve solo sus propios barcos)
5. Los barcos no pueden solaparse ni salirse del tablero

#### Fase 2: Ataque por Turnos
1. Una vez ambos jugadores terminan de colocar sus barcos, comienza la fase de ataque
2. Por turnos, cada usuario ve un tablero 16x16 VACÍO (sin mostrar barcos del oponente)
3. El usuario hace clic en un cuadro del tablero para disparar un cañón
4. El sistema informa al usuario si:
   - **HIT**: Golpeó un barco del oponente
   - **MISS**: Falló (tocó agua)
5. Solo se puede disparar una vez por turno
6. El turno alterna entre ambos jugadores

#### Fase 3: Hundimiento de Barcos
1. Un barco se hunde cuando se disparan en TODOS sus cuadros
2. Ejemplo: Un barco de 3 cuadros requiere 3 impactos para hundirse
3. El sistema notifica cuando un barco es hundido
4. El usuario puede ver en qué cuadros ha disparado y el resultado

#### Condiciones de Victoria
- Gana el jugador que hunde todos los barcos del oponente
- Alternativamente, gana quien hunda más barcos si se establece un límite de turnos

### Representación del Tablero
- **0**: Agua (no disparado)
- **1**: Barco (posición inicial)
- **2**: Tocado (barco golpeado)
- **3**: Agua (disparo fallido)
- **4**: Hundido (barco completamente destruido)

## Flujo de Usuario Completo

### Fase 1: Registro y Login
1. **Registro**: Usuario crea cuenta → Validación en tiempo real → Encriptación de contraseña → Almacenamiento en BD
2. **Login**: Autenticación en BD → Conexión TCP/IP a servidor → Recibe estadísticas (victorias/derrotas) → Acceso al LobbyGUI

### Fase 2: Gestión de Salas
3. **Lobby**: Visualización de salas disponibles → Botones "Crear Sala" y "Unirse a Sala"
4. **Crear Sala (Player1)**:
   - Player1 hace clic en "Crear Sala"
   - Server crea GameRoom y asigna Player1
   - Server envía `ROOM_CREATED` y `AUTO_JOINED` a Player1
   - Player1 cierra LobbyGUI y abre RoomWaitingGUI
   - Sala aparece en listado con estado "WAITING, 1/2"

5. **Unirse a Sala (Player2)**:
   - Player2 ve sala en listado → Hace clic en "Unirse"
   - Server envía `ROOM_INFO` a Player2 (con nombre de Player1)
   - Server envía `PLAYER_JOINED` a Player1 (con nombre de Player2)
   - Player2 cierra LobbyGUI y abre RoomWaitingGUI
   - Player1 actualiza RoomWaitingGUI con nombre de Player2
   - Sala aparece en listado con estado "WAITING, 2/2"

### Fase 3: Colocación de Barcos
6. **Conexión Completa**:
   - Server cambia estado de GameRoom a `PLACING_SHIPS`
   - Server envía `Start_Placing_Ships` a ambos jugadores
   - Ambos jugadores cierran RoomWaitingGUI y abren ShipPlacementGUI

7. **Colocación de Barcos (Cada jugador independientemente)**:
   - Interfaz muestra: Lista de 5 barcos (tamaños: 2,3,4,5,5) + Tablero 16x16 + Panel de estado
   - Sistema clic-clic:
     a. Clic en barco de lista → Selecciona barco
     b. (Opcional) Tecla R → Rota barco (horizontal ↔ vertical)
     c. Hover sobre tablero → Muestra guía (verde = válido, rojo = inválido)
     d. Clic en celda → Coloca barco en esa posición
     e. Clic en barco colocado → Elimina y devuelve a lista
   - Validación en tiempo real:
     - Límites: Barco no puede salir del tablero 16x16
     - Solapamiento: Barcos no pueden superponerse
   - Botón "¡LISTO!" → Habilitado solo cuando 5 barcos colocados
   - Al hacer clic en "¡LISTO!" → Envia `PLACE_SHIPS:x,y,o;x,y,o;...` al servidor

8. **Servidor Valida**:
   - Parser de coordenadas de 5 barcos
   - Verificación de límites en servidor (seguridad adicional)
   - Verificación de solapamiento en servidor (seguridad adicional)
   - Colocación en tablero del servidor (board1 para Player1, board2 para Player2)
   - Envía `SHIPS_PLACED_OK` si válido
   - Envía `ERROR:mensaje` si inválido

9. **Juego Inicia**:
   - Cuando ambos jugadores reciben `SHIPS_PLACED_OK`
   - Server envía `GAME_START:oponente` a ambos
   - ShipPlacementGUI muestra mensaje "¡El juego comienza!"
   - ShipPlacementGUI se cierra
   - (Pendiente) Se abre GameGUI para la fase de ataque

### Fase 4: Ataque por Turnos (Por Implementar)
10. **Ataque por turnos**: Disparar al tablero del oponente hasta hundir sus barcos
11. **Fin del juego**: Se determina el ganador y se actualizan estadísticas en BD

## Características Técnicas
- Base de datos: PostgreSQL
- Comunicación: Sockets TCP/IP
- UI: Swing (JavaFX considerado para futuras versiones)
- Validación: Tiempo real en formularios
- Seguridad: Encriptación de contraseñas

## Estado Actual
- ✅ Sistema de autenticación funcional
- ✅ UI de login y registro implementadas
- ✅ Monitoreo básico operativo
- ✅ Sistema de salas funcional (creación/unión/listado)
- ✅ Comunicación cliente-servidor establecida
- ✅ Notificación de jugador unido implementada
- ✅ Actualización de tabla de salas mejorada
- ✅ Feedback visual de unión a sala mejorado
- ✅ **BUG: Creador de sala se une automáticamente ARREGLADO**
- ✅ **BUG: Player2 puede unirse sin errores ARREGLADO**
- ✅ **BUG: Información de salas mal mostrada ARREGLADO**
- ✅ **BUG: ShipPlacementGUI no se desplegaba para ambos usuarios ARREGLADO**
- ✅ **BUG: Drag & Drop no funcionaba REEMPLAZADO por clic-clic**
- ✅ **BUG: Lobby del jugador 2 no desaparecía ARREGLADO**
- ✅ **RoomWaitingGUI implementada**
- ✅ **ShipPlacementGUI implementada con sistema clic-clic**
- ✅ **Ship.java modelo implementado**
- ✅ **Representación visual de barcos (cuadrados ■)**
- ✅ **Rotación de barcos con tecla R**
- ✅ **Validación de colocación de barcos (líneas guía, colores)**
- ✅ **Click en barco colocado para eliminar**
- ✅ **Parser de colocación de barcos implementado en servidor**
- ✅ **Validación de superposición de barcos implementada en cliente y servidor**
- ✅ **Protocolo de comunicación para colocación de barcos completo**
- 🔄 Mejoras de UI en proceso
- 🚧 Fase de ataque por turnos pendiente (GUI faltante)

## Bugs Recientes - Solucionados

### Bug 1: Creador de Sala No Se Une Automáticamente
**Problema**: Cuando el Jugador 1 crea una sala, se queda en el Lobby y el Jugador 2 no puede unirse (error "sala llena o en juego")

**Causa**: El creador de la sala (Jugador 1) se establecía como player1 en el constructor pero nunca "se unía formalmente", por lo que:
- El servidor no lo consideraba como "en la sala"
- La tabla de salas no mostraba su estado correctamente
- Player2 recibía error al intentar unirse

**Solución Implementada**:
1. Modificado `GameRoom.java` constructor para enviar notificación `AUTO_JOINED` al creador
2. Modificado estado inicial a `WAITING` (antes no estaba establecido)
3. Implementado manejo de `AUTO_JOINED` en `ClientController.java`
4. Implementado `LobbyGUI.onAutoJoined()` para actualizar estadísticas y deshabilitar botón "Crear Sala"
5. Añadido `Protocol.AUTO_JOINED` como constante oficial

**Resultado**: Cuando el Jugador 1 crea una sala:
- Recibe notificación de unión automática
- Estadísticas se actualizan: "Sala creada: Sala-1 (Esperando oponente)"
- Botón "Crear Sala" se deshabilita
- Tabla muestra estado correcto
- Jugador 2 puede unirse sin errores

### Bug 2: Información de Salas Mal Mostrada en Lobby
**Problema**: La tabla de salas en LobbyGUI mostraba información incorrecta:
- Columnas no coincidían con los datos
- Estado no se mostraba porque `room.getState()` enviaba el objeto enum en lugar de su nombre string
- Validación de unión a sala verificaba columna equivocada

**Causa**:
- Columnas definidas: `{"ID Sala", "Jugador 1", "Estado", "Jugadores"}` pero datos tenían 5 campos
- `room.getState()` en `RoomManager.getRoomListString()` no usaba `.name()`
- `LobbyGUI.handleJoinRoom()` verificaba columna 2 (Jugador 2) en lugar de columna 3 (Estado)

**Solución Implementada**:
1. Actualizado columnas de tabla en LobbyGUI: `{"ID Sala", "Jugador 1", "Jugador 2", "Estado", "Jugadores"}`
2. Modificado `RoomManager.getRoomListString()` para usar `room.getState().name()`
3. Actualizado `LobbyGUI.handleJoinRoom()` para verificar columna 3 (Estado)
4. Simplificado formato de visualización de jugadores (sin prefijo "Jugador 1: ")

**Resultado**: Tabla de salas muestra información correcta con 5 columnas y estado traducido

### Bug 3: ShipPlacementGUI No Se Desplegaba para Ambos Usuarios
**Problema**: Cuando el servidor enviaba `Start_Placing_Ships`:
- Player1 abría ShipPlacementGUI (RoomWaitingGUI → ShipPlacementGUI)
- Player2 se quedaba en RoomWaitingGUI (no abría ShipPlacementGUI)

**Causa**: `ClientController` solo abría ShipPlacementGUI si `currentView` era `RoomWaitingGUI`

**Solución Implementada**:
1. Modificado `ClientController` caso `Start_Placing_Ships` para abrir ShipPlacementGUI independientemente de la vista actual
2. Ambos jugadores ahora cierran su vista actual y abren ShipPlacementGUI

**Resultado**: Ambos jugadores (Player1 y Player2) abren su propia ShipPlacementGUI correctamente

### Bug 4: Drag & Drop No Funcionaba en ShipPlacementGUI
**Problema**: El sistema de drag & drop no funcionaba:
- Al arrastrar un barco de la lista al tablero, no ocurría nada
- Solo se veía el tamaño del barco al hacer hover sobre el tablero

**Causa**: El enfoque con `DropTarget` era complejo y no funcionaba bien con Swing

**Solución Implementada**:
1. Reemplazado drag & drop por sistema **clic-clic** más simple e intuitivo
2. Eliminado: DropTarget, TransferHandler complejo, DataFlavor personalizado
3. Implementado:
   - Clic en barco de lista → Selecciona
   - Clic en tablero → Coloca
   - Tecla R → Rota barco seleccionado
   - Clic en barco colocado → Elimina
4. Agregada representación visual de barcos con cuadrados "■"

**Resultado**: Sistema de colocación funciona correctamente sin drag & drop

### Bug 5: Lobby del Jugador 2 No Desaparecía
**Problema**: Cuando Player1 recibía `Start_Placing_Ships`:
- RoomWaitingGUI de Player1 se cerraba
- RoomWaitingGUI de Player2 permanecía abierta

**Causa**: Esto estaba relacionado con el Bug 3 y se solucionó indirectamente

**Solución**: Corrección del Bug 3 (ShipPlacementGUI se abre independientemente de la vista actual) también solucionó este bug

**Resultado**: Tanto Player1 como Player2 cierran su RoomWaitingGUI y abren ShipPlacementGUI

## Discrepancias con Implementación Actual

### Tamaño del Tablero ✅
- **REQUERIDO**: 16x16 cuadros
- **ACTUAL**: 16x16 cuadros (GameRoom.java:14-15)
- **ESTADO**: ✅ CORRECTO - Tamaño actualizado en GameRoom y ShipPlacementGUI

### Barcos ✅
- **REQUERIDO**: 5 barcos (tamaños 2, 3, 4, 5, 5)
- **ACTUAL**: ✅ Implementado en Ship.java y ShipPlacementGUI
- **ESTADO**: ✅ CORRECTO - Clase Ship con tamaño, orientación, posición y estado creada

### Fase de Colocación ✅
- **REQUERIDO**: GUI individual para cada jugador
- **ACTUAL**: ✅ ShipPlacementGUI implementada para cada jugador
- **ESTADO**: ✅ CORRECTO - GUI funcional con clic-clic, rotación, validación

### Fase de Ataque 🚧
- **REQUERIDO**: GUI de juego con tablero vacío y disparos por turnos
- **ACTUAL**: ❌ Solo envío de mensajes YOUR_TURN/OPPONENT_TURN sin GUI
- **ACCIÓN**: Crear GameGUI.java

### Hundimiento de Barcos 🚧
- **REQUERIDO**: Barco hundido cuando todos sus cuadros son disparados
- **ACTUAL**: ⚠️ checkShipSunk() existe pero no implementa lógica real
- **ACCIÓN**: Implementar lógica de hundimiento en checkShipSunk() y checkWinCondition()

## Tecnologías Utilizadas
- Java 17 (Release 17 para target)
- PostgreSQL 42.7.2
- Swing/AWT (javax.swing, java.awt)
- Sockets TCP/IP (java.net.Socket, java.net.ServerSocket)
- Hibernate 5.6.15 (ORM)
- Maven 3.13.0 (Gestión de dependencias)
- bcrypt 0.4 (Encriptación de contraseñas)

## Arquitectura Técnica

### Patrón de Diseño
- **Singleton**: ClientController (instancia única por cliente)
- **MVC (Model-View-Controller)**:
  - **Model**: Ship, GameRoom, Player
  - **View**: LoginGUI, LobbyGUI, RoomWaitingGUI, ShipPlacementGUI, MonitorGUI
  - **Controller**: ClientController, ClientHandler, RoomManager

### Flujo de Comunicación
1. **Cliente → Servidor**:
   - ClientController envía mensajes vía TCP/IP socket
   - Formato: `COMANDO:parametro1:parametro2:...`
   - Ejemplos: `LOGIN:usuario:password`, `CREATE_ROOM`, `PLACE_SHIPS:0,0,0;2,3,1;...`

2. **Servidor → Cliente**:
   - ClientHandler procesa comandos en hilo separado
   - Llama a RoomManager o GameRoom según comando
   - Responde con mensajes en mismo formato
   - Ejemplos: `LOGIN_OK:3:2`, `ROOM_CREATED:Sala-1`, `SHIPS_PLACED_OK`

### Concurrencia
- **Servidor**: Un hilo por cliente (ClientHandler extends Runnable)
- **Cliente**: Hilo de escucha separado (listenToServer) para no bloquear UI
- **UI Swing**: SwingUtilities.invokeLater() para actualizar UI desde otros hilos

### Manejo de Estado del Cliente
ClientController mantiene:
- `playerName`: Nombre del jugador autenticado
- `currentView`: JFrame actual (para transiciones entre GUIs)
- `currentRoomId`: ID de sala actual (para unirse)
- `victorias/derrotas`: Estadísticas del jugador
- `socket/out/in`: Conexión TCP/IP con servidor

### Manejo de Estado del Servidor
GameRoom mantiene:
- `roomId`: Identificador único
- `player1/player2`: ClientHandlers de los 2 jugadores
- `currentState`: Enum (WAITING, PLACING_SHIPS, PLAYING, FINISHED)
- `board1/board2`: Tableros 16x16 (int[][])
- `p1ShipsReady/p2ShipsReady`: Flags de colocación
- `isPlayer1Turn`: Control de turnos
- Estadísticas: p1TotalShots, p2TotalShots, p1Hits, p2Hits, p1ShipsSunk, p2ShipsSunk

### Seguridad
- **Contraseñas**: Encriptación con bcrypt (PasswordUtil)
- **Validación**: InputValidator para datos de entrada
- **Validación en Servidor**: Doble validación (cliente + servidor) para evitar trampas
- **Error Handling**: Manejo robusto de desconexiones y errores

## Objetivos - Fase 1

### Goal Principal
Alcanzar la conexión de 2 usuarios en 1 sala y mostrar la interfaz para colocar los barcos.

### Tareas Específicas
1. ✅ Conectar 2 usuarios en una sala
2. ✅ Mostrar mensaje "Start_Placing_Ships" cuando ambos estén listos
3. ✅ Crear **ShipPlacementGUI** (interfaz para colocar barcos)
4. ✅ Implementar parser de datos de colocación de barcos
5. ✅ Validar que los barcos no se solapen
6. ✅ Validar que los barcos estén dentro del tablero
7. ✅ Implementar lógica para detectar cuando ambos jugadores terminaron
8. ✅ Transición a la fase de ataque cuando ambos listos (GAME_START enviado)

### Componentes Creados
- ✅ `ShipPlacementGUI.java`: Interfaz Swing para colocar 5 barcos en tablero 16x16
- ✅ `Ship.java`: Modelo de barco (tamaño, orientación, coordenadas, estado)
- ✅ `RoomWaitingGUI.java`: Interfaz de sala de espera con 2 jugadores
- 🚧 `Board.java`: Modelo de tablero con métodos de validación (por implementar si se necesita)
- 🚧 `ShipPlacementListener`: Interface para manejar eventos de colocación (por implementar si se necesita)

### Protocolo de Comunicación - Colocación de Barcos ✅
```
Cliente → Servidor: PLACE_SHIPS:coordenadas_comprimidas
Formato: x1,y1,o1;x2,y2,o2;x3,y3,o3;x4,y4,o4;x5,y5,o5
Donde:
- x,y = coordenada de inicio del barco
- o = orientación (0=horizontal, 1=vertical)
- Ejemplo: 0,0,0;2,3,1;5,5,0;8,8,1;12,10,0

Servidor → Cliente: SHIPS_PLACED_OK
Servidor → Cliente: ERROR:mensaje (si validación falla)
Servidor → Cliente (ambos): GAME_START:oponente (cuando ambos listos)
```

### Pendientes para Fase 2 (Ataque por Turnos)
- Crear GameGUI.java (interfaz de juego con tablero vacío)
- Implementar lógica de disparos por turnos
- Implementar notificación de HIT/MISS
- Implementar lógica de hundimiento de barcos
- Implementar condición de victoria
- Actualizar estadísticas en BD al terminar partida

### Pendientes Generales
- 🚧 Implementar `checkShipSunk()` en GameRoom para detectar hundimiento real de barcos
- 🚧 Incrementar contadores p1ShipsSunk/p2ShipsSunk cuando se hundan barcos
- 🚧 Notificar a jugadores cuando un barco es hundido