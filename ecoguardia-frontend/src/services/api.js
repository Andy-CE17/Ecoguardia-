import axios from 'axios';

// URL base del backend Spring Boot.
// En local apunta a tu maquina; cambia el host si pruebas desde otro dispositivo.
export const API_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_URL,
  headers: { 'Content-Type': 'application/json' },
});

// ---- Mediciones ----
export const getUltimasMediciones = () => api.get('/api/mediciones');
export const enviarMedicion = (data) => api.post('/api/mediciones', data);

// ---- Ubicaciones (aulas) ----
export const getUbicaciones = () => api.get('/api/ubicaciones');
export const crearUbicacion = (data) => api.post('/api/ubicaciones', data);
export const actualizarUbicacion = (id, data) => api.put(`/api/ubicaciones/${id}`, data);
export const eliminarUbicacion = (id) => api.delete(`/api/ubicaciones/${id}`);

// ---- Dispositivos ----
export const getDispositivos = () => api.get('/api/dispositivos');
export const getResumenDispositivos = () => api.get('/api/dispositivos/resumen');

// ---- Estadisticas ----
export const getEstadisticas = (desde, hasta) =>
  api.get('/api/estadisticas', { params: { desde, hasta } });

export default api;
