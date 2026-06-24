package com.ecoguardia.repository;

import com.ecoguardia.model.EstadoRuido;
import com.ecoguardia.model.Medicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicionRepository extends JpaRepository<Medicion, Long> {

    /** Ultimas mediciones ordenadas de la mas reciente a la mas antigua. */
    List<Medicion> findTop30ByOrderByFechaHoraDesc();

    /** Ultima medicion de una ubicacion concreta. */
    Medicion findTopByUbicacionIdOrderByFechaHoraDesc(Long ubicacionId);

    /** Mediciones dentro de un rango de fechas (para reportes). */
    List<Medicion> findByFechaHoraBetweenOrderByFechaHoraAsc(
            LocalDateTime desde, LocalDateTime hasta);

    /** Mediciones de una ubicacion dentro de un rango de fechas. */
    List<Medicion> findByUbicacionIdAndFechaHoraBetweenOrderByFechaHoraAsc(
            Long ubicacionId, LocalDateTime desde, LocalDateTime hasta);

    /** Promedio de nivel en un rango. */
    @Query("SELECT AVG(m.nivelDb) FROM Medicion m " +
           "WHERE m.fechaHora BETWEEN :desde AND :hasta")
    Double promedioNivel(@Param("desde") LocalDateTime desde,
                         @Param("hasta") LocalDateTime hasta);

    /** Maximo nivel en un rango. */
    @Query("SELECT MAX(m.nivelDb) FROM Medicion m " +
           "WHERE m.fechaHora BETWEEN :desde AND :hasta")
    Double maximoNivel(@Param("desde") LocalDateTime desde,
                       @Param("hasta") LocalDateTime hasta);

    /** Total de mediciones en un rango. */
    long countByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    /** Cuantas mediciones tuvieron cierto estado en un rango. */
    long countByEstadoAndFechaHoraBetween(
            EstadoRuido estado, LocalDateTime desde, LocalDateTime hasta);
}
