package com.ecoguardia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Datos que el ESP32 envia por HTTP POST.
 * Solo manda el nivel y su codigo; el estado y la fecha los pone el servidor.
 */
@Data
public class MedicionRequest {

    @NotNull(message = "El nivel de ruido es obligatorio")
    private BigDecimal nivelDb;

    /** Codigo del ESP32 que envia, ej. "ESP32-A1B2C3". */
    @NotNull(message = "El codigo del dispositivo es obligatorio")
    private String codigoEsp32;
}
