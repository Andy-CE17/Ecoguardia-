# EcoGuardia del Ruido — Backend (Spring Boot)

API que recibe mediciones de ruido de sensores ESP32, las guarda en PostgreSQL
y las empuja en vivo al dashboard por WebSocket.

## Requisitos

- Java 17 o superior
- Maven 3.8+
- PostgreSQL 14+

## Puesta en marcha

1. Crea la base de datos en PostgreSQL:

   ```sql
   CREATE DATABASE ecoguardia;
   ```

2. Ajusta usuario y contraseña en `src/main/resources/application.properties`
   si los tuyos son distintos a `postgres / postgres`.

3. Arranca el proyecto:

   ```bash
   mvn spring-boot:run
   ```

   Hibernate crea las tablas automáticamente (`ddl-auto=update`).
   La API queda en `http://localhost:8080`.

## Endpoints

### Mediciones

| Método | Ruta | Quién lo usa | Descripción |
|--------|------|--------------|-------------|
| POST | `/api/mediciones` | ESP32 | Envía una lectura de ruido |
| GET | `/api/mediciones` | Dashboard | Últimas 30 mediciones |

Ejemplo de lo que envía el ESP32:

```json
POST /api/mediciones
{
  "nivelDb": 65.4,
  "codigoEsp32": "ESP32-A1B2C3"
}
```

El servidor calcula el estado (BAJO/MEDIO/ALTO), pone la fecha y lo guarda.

### Aulas (ubicaciones)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/ubicaciones` | Listar aulas |
| POST | `/api/ubicaciones` | Crear aula |
| PUT | `/api/ubicaciones/{id}` | Editar aula |
| DELETE | `/api/ubicaciones/{id}` | Eliminar aula |

### Dispositivos (ESP32)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/dispositivos` | Listar dispositivos |
| GET | `/api/dispositivos/resumen` | Total / conectados / desconectados |
| POST | `/api/dispositivos` | Registrar dispositivo |
| PUT | `/api/dispositivos/{id}` | Editar dispositivo |
| DELETE | `/api/dispositivos/{id}` | Eliminar dispositivo |

### Estadísticas (reportes)

```
GET /api/estadisticas?desde=2026-06-01T00:00:00&hasta=2026-06-04T23:59:59
```

Devuelve promedio, máximo, total de mediciones y % en estado Alto.

## WebSocket (tiempo real)

- Punto de conexión: `ws://localhost:8080/ws` (con SockJS)
- Topic a suscribirse: `/topic/mediciones`

Cada vez que llega una medición nueva del ESP32, se publica en ese topic
y el dashboard la recibe al instante.

Ejemplo de cliente en React:

```javascript
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const client = new Client({
  webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
  onConnect: () => {
    client.subscribe('/topic/mediciones', (msg) => {
      const medicion = JSON.parse(msg.body);
      console.log('Nueva medición:', medicion);
    });
  },
});
client.activate();
```

## Notas

- Los umbrales de clasificación (60 dB y 80 dB) están en `MedicionService`.
  Más adelante pueden venir de una tabla de configuración editable.
- La autenticación (JWT) no está incluida en esta fase; se agrega como capa
  posterior sin tocar esta lógica.
- El endpoint de generación de PDF (ReportLab/iText) no está aquí todavía;
  las estadísticas que alimentan el reporte sí lo están.
