import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Menu } from 'lucide-react';
import Sidebar from './Sidebar';

export default function Layout() {
  const [abierto, setAbierto] = useState(false);

  return (
    <div className="layout">
      <Sidebar abierto={abierto} onCerrar={() => setAbierto(false)} />

      {/* Overlay oscuro detras del sidebar en movil */}
      {abierto && <div className="overlay" onClick={() => setAbierto(false)} />}

      <main className="contenido">
        <button
          className="menu-btn"
          onClick={() => setAbierto(true)}
          aria-label="Abrir menú"
          style={{ marginBottom: '1rem' }}
        >
          <Menu size={20} />
        </button>
        <Outlet />
      </main>
    </div>
  );
}
