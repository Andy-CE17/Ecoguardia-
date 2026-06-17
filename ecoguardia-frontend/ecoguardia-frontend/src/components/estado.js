/**
 * Devuelve la clase CSS y la etiqueta legible para un estado de ruido.
 * El backend envia "BAJO" | "MEDIO" | "ALTO".
 */
export function estadoInfo(estado) {
  switch (estado) {
    case 'BAJO':  return { clase: 'bajo',  label: 'Bajo' };
    case 'MEDIO': return { clase: 'medio', label: 'Medio' };
    case 'ALTO':  return { clase: 'alto',  label: 'Alto' };
    default:      return { clase: 'bajo',  label: estado || '—' };
  }
}

/** Color de linea/punto segun estado, para el grafico. */
export function colorEstado(estado) {
  switch (estado) {
    case 'ALTO':  return '#A32D2D';
    case 'MEDIO': return '#BA7517';
    default:      return '#639922';
  }
}
