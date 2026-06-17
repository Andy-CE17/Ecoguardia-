package com.ecoguardia.service;

import com.ecoguardia.dto.EstadisticasResponse;
import com.ecoguardia.model.EstadoRuido;
import com.ecoguardia.repository.MedicionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Calcula los resumenes estadisticos que muestra la vista de reportes.
 */
@Service
public class EstadisticaService {

    private final MedicionRepository medicionRepository;

    public EstadisticaService(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public EstadisticasResponse calcular(LocalDateTime desde, LocalDateTime hasta) {
        Double promedio = medicionRepository.promedioNivel(desde, hasta);
        Double maximo = medicionRepository.maximoNivel(desde, hasta);
        long total = medicionRepository.countByFechaHoraBetween(desde, hasta);
        long enAlto = medicionRepository.countByEstadoAndFechaHoraBetween(
                EstadoRuido.ALTO, desde, hasta);

        double porcentajeAlto = total > 0 ? (enAlto * 100.0 / total) : 0.0;

        return new EstadisticasResponse(
                promedio != null ? redondear(promedio) : 0.0,
                maximo != null ? redondear(maximo) : 0.0,
                total,
                redondear(porcentajeAlto)
        );
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
