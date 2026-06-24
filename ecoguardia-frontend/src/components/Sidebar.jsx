import { useState } from 'react';
import { NavLink } from 'react-router-dom';
import {
  Leaf, LayoutDashboard, Activity, Bell, Building2, DoorOpen,
  Cpu, BarChart3, FileText, Settings, LogOut, ChevronDown,
} from 'lucide-react';

// Definicion de los grupos desplegables del menu
const grupos = [
  {
    titulo: 'Monitoreo',
    icono: Activity,
    items: [
      { to: '/en-vivo', label: 'En vivo', icono: Activity },
      { to: '/alertas', label: 'Alertas', icono: Bell },
    ],
  },
  {
    titulo: 'Gestión',
    icono: Building2,
    items: [
      { to: '/aulas', label: 'Aulas', icono: DoorOpen },
      { to: '/dispositivos', label: 'Dispositivos', icono: Cpu },
    ],
  },
  {
    titulo: 'Reportes',
    icono: FileText,
    items: [
      { to: '/estadisticas', label: 'Estadísticas', icono: BarChart3 },
      { to: '/exportar', label: 'Exportar PDF', icono: FileText },
    ],
  },
];

export default function Sidebar({ abierto, onCerrar }) {
  // Todos los grupos empiezan abiertos
  const [expandidos, setExpandidos] = useState({
    Monitoreo: true, Gestión: true, Reportes: true,
  });

  const toggle = (titulo) =>
    setExpandidos((prev) => ({ ...prev, [titulo]: !prev[titulo] }));

  return (
    <nav className={`sidebar ${abierto ? 'abierto' : ''}`}>
      <div className="sidebar-logo">
        <Leaf size={24} color="var(--verde-suave)" />
        <span>EcoGuardia</span>
      </div>

      <NavLink
        to="/"
        end
        className={({ isActive }) => `sidebar-item ${isActive ? 'activo' : ''}`}
        onClick={onCerrar}
      >
        <LayoutDashboard size={18} />
        Panel principal
      </NavLink>

      {grupos.map((grupo) => {
        const Icono = grupo.icono;
        const abiertoGrupo = expandidos[grupo.titulo];
        return (
          <div key={grupo.titulo}>
            <button
              className="sidebar-grupo-titulo"
              onClick={() => toggle(grupo.titulo)}
            >
              <span className="izq">
                <Icono size={16} />
                {grupo.titulo}
              </span>
              <ChevronDown
                size={16}
                style={{
                  transition: 'transform 0.2s',
                  transform: abiertoGrupo ? 'rotate(0)' : 'rotate(-90deg)',
                }}
              />
            </button>
            {abiertoGrupo &&
              grupo.items.map((item) => {
                const ItemIcono = item.icono;
                return (
                  <NavLink
                    key={item.to}
                    to={item.to}
                    className={({ isActive }) =>
                      `sidebar-subitem ${isActive ? 'activo' : ''}`}
                    onClick={onCerrar}
                  >
                    <ItemIcono size={16} />
                    {item.label}
                  </NavLink>
                );
              })}
          </div>
        );
      })}

      <div className="sidebar-footer">
        <NavLink
          to="/configuracion"
          className={({ isActive }) => `sidebar-item ${isActive ? 'activo' : ''}`}
          onClick={onCerrar}
        >
          <Settings size={18} />
          Configuración
        </NavLink>
        <NavLink to="/login" className="sidebar-item" onClick={onCerrar}>
          <LogOut size={18} />
          Cerrar sesión
        </NavLink>
      </div>
    </nav>
  );
}
