package com.ecoguardia.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

/**
 * Resumen estadistico de un rango de fechas, para la vista de reportes.
 */
@Data
@AllArgsConstructor
public class EstadisticasResponse {
    private double promedio;
    private double maximo;
    private long totalMediciones;
    private double porcentajeAlto;
}
