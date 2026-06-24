import { useEffect, useState, useRef } from 'react';
import {
  LineChart, Line, XAxis, YAxis, ReferenceLine,
  ResponsiveContainer, Tooltip, CartesianGrid,
} from 'recharts';
import { Leaf, DoorOpen } from 'lucide-react';
import { getUltimasMediciones, getUbicaciones } from '../services/api';
import { conectarWebSocket } from '../services/websocket';
import { estadoInfo } from '../components/estado';

export default function PanelPrincipal() {
  const [mediciones, setMediciones] = useState([]); // historico para el grafico
  const [aulas, setAulas] = useState([]);            // ultimas por aula
  const [conectado, setConectado] = useState(false);
  const clienteRef = useRef(null);

  // Carga inicial: historial + aulas
  useEffect(() => {
    getUltimasMediciones()
      .then((res) => {
        // El backend devuelve de la mas reciente a la mas antigua; invertimos
        // para que el grafico vaya de izquierda (antigua) a derecha (nueva).
        const ordenadas = [...res.data].reverse();
        setMediciones(ordenadas);
      })
      .catch((e) => console.error('Error cargando mediciones', e));

    getUbicaciones()
      .then((res) => setAulas(res.data.map((a) => ({ ...a, ultima: null }))))
      .catch((e) => console.error('Error cargando aulas', e));
  }, []);

  // Conexion WebSocket para datos en vivo
  useEffect(() => {
    const cliente = conectarWebSocket(
      (medicion) => {
        // Agrega la nueva medicion al grafico (manteniendo las ultimas 30)
        setMediciones((prev) => [...prev, medicion].slice(-30));
        // Actualiza el estado de su aula
        if (medicion.ubicacionId) {
          setAulas((prev) =>
            prev.map((a) =>
              a.id === medicion.ubicacionId ? { ...a, ultima: medicion } : a
            )
          );
        }
      },
      (estado) => setConectado(estado)
    );
    clienteRef.current = cliente;
    return () => cliente?.deactivate();
  }, []);

  // ---- Metricas derivadas ----
  const ultima = mediciones[mediciones.length - 1];
  const nivelActual = ultima ? Number(ultima.nivelDb).toFixed(0) : '—';
  const estadoActual = ultima ? estadoInfo(ultima.estado).label : '—';
  const aulasActivas = aulas.length;
  const picoHoy = mediciones.length
    ? Math.max(...mediciones.map((m) => Number(m.nivelDb))).toFixed(0)
    : '—';

  // Datos para el grafico
  const datosGrafico = mediciones.map((m) => ({
    hora: new Date(m.fechaHora).toLocaleTimeString('es-PE', {
      hour: '2-digit', minute: '2-digit',
    }),
    nivel: Number(m.nivelDb),
  }));

  return (
    <>
      {/* Encabezado */}
      <div className="dash-top">
        <div className="dash-titulo">
          <Leaf size={26} color="var(--verde-medio)" />
          <div>
            <h1>EcoGuardia del Ruido</h1>
            <p>Panel principal · en vivo</p>
          </div>
        </div>
        <span className={`conexion ${conectado ? 'on' : 'off'}`}>
          <span className="punto" />
          {conectado ? 'Conectado' : 'Desconectado'}
        </span>
      </div>

      {/* Tarjetas de metricas */}
      <div className="grid-metrics">
        <div className="metric-card">
          <div className="label">Nivel actual</div>
          <div className="value" style={{ color: 'var(--medio-texto)' }}>
            {nivelActual} dB
          </div>
        </div>
        <div className="metric-card">
          <div className="label">Estado</div>
          <div className="value" style={{ color: 'var(--medio-texto)' }}>
            {estadoActual}
          </div>
        </div>
        <div className="metric-card">
          <div className="label">Aulas activas</div>
          <div className="value">{aulasActivas}</div>
        </div>
        <div className="metric-card">
          <div className="label">Pico de hoy</div>
          <div className="value" style={{ color: 'var(--alto-texto)' }}>
            {picoHoy} dB
          </div>
        </div>
      </div>

      {/* Grafico + lista de aulas */}
      <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0,2fr) minmax(260px,1fr)', gap: 16 }}
           className="dash-grid">
        <div className="card">
          <p style={{ fontSize: 16, fontWeight: 600, marginBottom: 14 }}>
            Nivel de ruido · últimos 30 min
          </p>
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={datosGrafico} margin={{ top: 10, right: 16, left: -10, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="#eef0ec" />
              <XAxis dataKey="hora" tick={{ fontSize: 11, fill: '#9CA3AF' }} />
              <YAxis domain={[30, 100]} tick={{ fontSize: 11, fill: '#9CA3AF' }} />
              <Tooltip />
              <ReferenceLine y={60} stroke="#BA7517" strokeDasharray="4 4" />
              <ReferenceLine y={80} stroke="#A32D2D" strokeDasharray="4 4" />
              <Line type="monotone" dataKey="nivel" stroke="#639922"
                    strokeWidth={2} dot={false} isAnimationActive={false} />
            </LineChart>
          </ResponsiveContainer>
          <div style={{ display: 'flex', gap: 16, marginTop: 10, fontSize: 12, color: 'var(--texto-secundario)' }}>
            <span><span style={{ color: '#639922' }}>●</span> Bajo (&lt;60 dB)</span>
            <span><span style={{ color: '#BA7517' }}>●</span> Medio (60–80 dB)</span>
            <span><span style={{ color: '#A32D2D' }}>●</span> Alto (&gt;80 dB)</span>
          </div>
        </div>

        <div className="card">
          <p style={{ fontSize: 16, fontWeight: 600, marginBottom: 6 }}>
            Aulas monitoreadas
          </p>
          {aulas.length === 0 && (
            <p style={{ fontSize: 13, color: 'var(--texto-secundario)', paddingTop: 8 }}>
              No hay aulas registradas todavía.
            </p>
          )}
          {aulas.map((aula) => {
            const info = aula.ultima ? estadoInfo(aula.ultima.estado) : null;
            return (
              <div key={aula.id} className="aula-row">
                <span className="aula-nombre">
                  <DoorOpen size={18} color="var(--texto-secundario)" />
                  <span>
                    <span style={{ display: 'block' }}>{aula.nombre}</span>
                    <span className="aula-db">
                      {aula.ultima ? `${Number(aula.ultima.nivelDb).toFixed(0)} dB` : 'sin datos'}
                    </span>
                  </span>
                </span>
                {info && <span className={`badge ${info.clase}`}>{info.label}</span>}
              </div>
            );
          })}
        </div>
      </div>
    </>
  );
}
