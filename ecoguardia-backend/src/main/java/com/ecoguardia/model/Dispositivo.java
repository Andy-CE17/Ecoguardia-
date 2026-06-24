package com.ecoguardia.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa un sensor ESP32 fisico registrado en el sistema.
 * Mapea a la tabla "dispositivos".
 */
@Entity
@Table(name = "dispositivos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre legible del sensor, ej. "Sensor ESP32-01". */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Identificador unico del hardware, ej. "ESP32-A1B2C3". */
    @Column(name = "codigo_esp32", nullable = false, unique = true, length = 50)
    private String codigoEsp32;

    /** Aula a la que esta asignado este dispositivo. */
    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    /** Fecha y hora de la ultima vez que envio datos. */
    @Column(name = "ultima_lectura")
    private LocalDateTime ultimaLectura;

    /** true si envio datos recientemente, false si se considera caido. */
    @Column(nullable = false)
    private boolean conectado = false;
}
