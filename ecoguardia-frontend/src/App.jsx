import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import PanelPrincipal from './pages/PanelPrincipal';
import EnConstruccion from './pages/EnConstruccion';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<PanelPrincipal />} />
          <Route path="en-vivo" element={<PanelPrincipal />} />
          <Route path="alertas" element={<EnConstruccion titulo="Alertas" />} />
          <Route path="aulas" element={<EnConstruccion titulo="Gestión de aulas" />} />
          <Route path="dispositivos" element={<EnConstruccion titulo="Gestión de dispositivos" />} />
          <Route path="estadisticas" element={<EnConstruccion titulo="Estadísticas" />} />
          <Route path="exportar" element={<EnConstruccion titulo="Exportar PDF" />} />
          <Route path="configuracion" element={<EnConstruccion titulo="Configuración" />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
