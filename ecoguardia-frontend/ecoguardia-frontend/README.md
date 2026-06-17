# EcoGuardia del Ruido — Frontend (React + Vite)

Dashboard que muestra las mediciones de ruido en tiempo real, conectado al
backend de Spring Boot por REST y WebSocket.

## Requisitos

- Node.js 18 o superior
- El backend de EcoGuardia corriendo en `http://localhost:8080`

## Puesta en marcha

```bash
npm install
npm run dev
```

Abre `http://localhost:5173` en el navegador.

## Cómo ver el tiempo real funcionando

1. Arranca el backend (`mvn spring-boot:run`).
2. Arranca este frontend (`npm run dev`).
3. Abre el panel principal en el navegador.
4. Desde Thunder Client o Postman, haz un POST a
   `http://localhost:8080/api/mediciones` con:

   ```json
   { "nivelDb": 72, "codigoEsp32": "ESP32-A1B2C3" }
   ```

5. La medición aparece al instante en el gráfico, sin recargar la página.
   El indicador "Conectado" arriba a la derecha confirma el WebSocket activo.

## Estructura

```
src/
├── main.jsx                 punto de entrada
├── App.jsx                  rutas
├── components/
│   ├── Layout.jsx           sidebar + contenido + menú móvil
│   ├── Sidebar.jsx          navegación con grupos desplegables
│   └── estado.js            helper de colores/etiquetas de estado
├── pages/
│   ├── PanelPrincipal.jsx   dashboard en vivo (esta fase)
│   └── EnConstruccion.jsx   placeholder de las demás vistas
├── services/
│   ├── api.js               llamadas REST (axios)
│   └── websocket.js         conexión STOMP/SockJS
└── styles/
    ├── global.css           variables de color y base
    └── components.css        estilos de layout y componentes
```

## Notas

- La URL del backend está en `src/services/api.js` (constante `API_URL`).
  Si pruebas desde otro dispositivo en la misma red, cámbiala por la IP
  de tu máquina, ej. `http://192.168.1.50:8080`.
- Las vistas Aulas, Dispositivos, Reportes y Configuración están como
  placeholder; se construirán en los siguientes pasos reutilizando los
  endpoints que ya tiene el backend.
