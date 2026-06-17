package com.ecoguardia.dto;

import com.ecoguardia.model.EstadoRuido;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Medicion ya procesada que se devuelve por REST o se empuja por WebSocket.
 */
@Data
@AllArgsConstructor
public class MedicionResponse {
    private Long id;
    private BigDecimal nivelDb;
    private EstadoRuido estado;
    private LocalDateTime fechaHora;
    private Long ubicacionId;
    private String ubicacionNombre;
}
