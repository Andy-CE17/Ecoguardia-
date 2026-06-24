package com.ecoguardia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa una medicion de ruido enviada por un ESP32.
 * Mapea a la tabla "mediciones_ruido".
 */
@Entity
@Table(name = "mediciones_ruido", indexes = {
        @Index(name = "idx_mediciones_fecha", columnList = "fecha_hora"),
        @Index(name = "idx_mediciones_ubicacion", columnList = "ubicacion_id"),
        @Index(name = "idx_mediciones_estado", columnList = "estado")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nivel de ruido en decibeles. */
    @Column(name = "nivel_db", nullable = false, precision = 5, scale = 2)
    private BigDecimal nivelDb;

    /** Estado calculado a partir del nivel y los umbrales configurados. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EstadoRuido estado;

    /** Momento del registro. Lo pone el servidor, no el ESP32. */
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    /** Aula donde se tomo la medicion (opcional). */
    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;
}
