package com.ecoguardia.repository;

import com.ecoguardia.model.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {

    /** Busca un dispositivo por su codigo de hardware. */
    Optional<Dispositivo> findByCodigoEsp32(String codigoEsp32);

    /** Cuenta cuantos dispositivos estan conectados o desconectados. */
    long countByConectado(boolean conectado);
}
