package com.ecoguardia.service;

import com.ecoguardia.dto.MedicionRequest;
import com.ecoguardia.dto.MedicionResponse;
import com.ecoguardia.model.Dispositivo;
import com.ecoguardia.model.EstadoRuido;
import com.ecoguardia.model.Medicion;
import com.ecoguardia.repository.DispositivoRepository;
import com.ecoguardia.repository.MedicionRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica central: recibe la medicion del ESP32, la clasifica, la guarda
 * y la empuja en vivo al dashboard por WebSocket.
 */
@Service
public class MedicionService {

    // Umbrales por defecto en dB. Idealmente vendrian de la configuracion.
    private static final BigDecimal UMBRAL_BAJO_MEDIO = new BigDecimal("60");
    private static final BigDecimal UMBRAL_MEDIO_ALTO = new BigDecimal("80");

    private final MedicionRepository medicionRepository;
    private final DispositivoRepository dispositivoRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MedicionService(MedicionRepository medicionRepository,
                           DispositivoRepository dispositivoRepository,
                           SimpMessagingTemplate messagingTemplate) {
        this.medicionRepository = medicionRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Procesa una medicion entrante del ESP32.
     */
    @Transactional
    public MedicionResponse registrar(MedicionRequest request) {
        // 1. Clasificar el estado segun el nivel
        EstadoRuido estado = clasificar(request.getNivelDb());

        // 2. Buscar el dispositivo y su aula asignada (si existe)
        Dispositivo dispositivo = dispositivoRepository
                .findByCodigoEsp32(request.getCodigoEsp32())
                .orElse(null);

        // 3. Crear y guardar la medicion
        Medicion medicion = new Medicion();
        medicion.setNivelDb(request.getNivelDb());
        medicion.setEstado(estado);
        medicion.setFechaHora(LocalDateTime.now());
        if (dispositivo != null) {
            medicion.setUbicacion(dispositivo.getUbicacion());
            // Actualizar el estado del dispositivo
            dispositivo.setUltimaLectura(LocalDateTime.now());
            dispositivo.setConectado(true);
            dispositivoRepository.save(dispositivo);
        }
        Medicion guardada = medicionRepository.save(medicion);

        // 4. Convertir a DTO
        MedicionResponse response = toResponse(guardada);

        // 5. Empujar en vivo al dashboard
        messagingTemplate.convertAndSend("/topic/mediciones", response);

        return response;
    }

    /**
     * Decide el estado a partir del nivel y los umbrales.
     */
    private EstadoRuido clasificar(BigDecimal nivel) {
        if (nivel.compareTo(UMBRAL_MEDIO_ALTO) >= 0) {
            return EstadoRuido.ALTO;
        } else if (nivel.compareTo(UMBRAL_BAJO_MEDIO) >= 0) {
            return EstadoRuido.MEDIO;
        } else {
            return EstadoRuido.BAJO;
        }
    }

    /** Ultimas 30 mediciones para poblar el grafico al cargar. */
    public List<MedicionResponse> ultimas() {
        return medicionRepository.findTop30ByOrderByFechaHoraDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MedicionResponse toResponse(Medicion m) {
        return new MedicionResponse(
                m.getId(),
                m.getNivelDb(),
                m.getEstado(),
                m.getFechaHora(),
                m.getUbicacion() != null ? m.getUbicacion().getId() : null,
                m.getUbicacion() != null ? m.getUbicacion().getNombre() : null
        );
    }
}
