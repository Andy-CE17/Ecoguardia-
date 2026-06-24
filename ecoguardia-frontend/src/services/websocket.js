import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { API_URL } from './api';

/**
 * Crea y conecta un cliente STOMP sobre WebSocket.
 *
 * @param {function} onMedicion  callback que recibe cada medicion nueva
 * @param {function} onEstado    callback con el estado de conexion (true/false)
 * @returns {Client} el cliente, para poder desconectarlo despues
 */
export function conectarWebSocket(onMedicion, onEstado) {
  const client = new Client({
    // SockJS da compatibilidad si el navegador no soporta WebSocket puro
    webSocketFactory: () => new SockJS(`${API_URL}/ws`),
    reconnectDelay: 5000, // reintenta cada 5s si se cae

    onConnect: () => {
      if (onEstado) onEstado(true);
      // Suscripcion al topic donde el backend publica las mediciones
      client.subscribe('/topic/mediciones', (mensaje) => {
        try {
          const medicion = JSON.parse(mensaje.body);
          onMedicion(medicion);
        } catch (e) {
          console.error('Error al parsear medicion:', e);
        }
      });
    },

    onDisconnect: () => {
      if (onEstado) onEstado(false);
    },

    onStompError: (frame) => {
      console.error('Error STOMP:', frame.headers['message']);
      if (onEstado) onEstado(false);
    },
  });

  client.activate();
  return client;
}
