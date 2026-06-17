package com.ecoguardia.controller;

import com.ecoguardia.dto.EstadisticasResponse;
import com.ecoguardia.service.EstadisticaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * Endpoint de estadisticas para la vista de reportes.
 *
 * Ejemplo:
 * GET /api/estadisticas?desde=2026-06-01T00:00:00&hasta=2026-06-04T23:59:59
 */
@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    private final EstadisticaService estadisticaService;

    public EstadisticaController(EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    @GetMapping
    public EstadisticasResponse obtener(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta) {
        return estadisticaService.calcular(desde, hasta);
    }
}
