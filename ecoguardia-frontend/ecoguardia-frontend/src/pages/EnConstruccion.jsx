import { Construction } from 'lucide-react';

export default function EnConstruccion({ titulo }) {
  return (
    <>
      <div className="page-header">
        <Construction size={22} color="var(--verde-medio)" />
        <div>
          <h1>{titulo}</h1>
          <p>Esta sección se construirá en el siguiente paso</p>
        </div>
      </div>
      <div className="card" style={{ textAlign: 'center', padding: '3rem 1rem', color: 'var(--texto-secundario)' }}>
        Próximamente
      </div>
    </>
  );
}
