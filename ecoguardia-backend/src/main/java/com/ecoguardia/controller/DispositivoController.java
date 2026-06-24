package com.ecoguardia.controller;

import com.ecoguardia.model.Dispositivo;
import com.ecoguardia.repository.DispositivoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * CRUD de dispositivos ESP32 para la vista "Gestion de dispositivos".
 */
@RestController
@RequestMapping("/api/dispositivos")
public class DispositivoController {

    private final DispositivoRepository dispositivoRepository;

    public DispositivoController(DispositivoRepository dispositivoRepository) {
        this.dispositivoRepository = dispositivoRepository;
    }

    @GetMapping
    public List<Dispositivo> listar() {
        return dispositivoRepository.findAll();
    }

    /** Tarjetas de resumen: total, conectados, desconectados. */
    @GetMapping("/resumen")
    public Map<String, Long> resumen() {
        return Map.of(
                "total", dispositivoRepository.count(),
                "conectados", dispositivoRepository.countByConectado(true),
                "desconectados", dispositivoRepository.countByConectado(false)
        );
    }

    @PostMapping
    public Dispositivo crear(@RequestBody Dispositivo dispositivo) {
        return dispositivoRepository.save(dispositivo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dispositivo> actualizar(
            @PathVariable Long id, @RequestBody Dispositivo datos) {
        return dispositivoRepository.findById(id)
                .map(d -> {
                    d.setNombre(datos.getNombre());
                    d.setCodigoEsp32(datos.getCodigoEsp32());
                    d.setUbicacion(datos.getUbicacion());
                    return ResponseEntity.ok(dispositivoRepository.save(d));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!dispositivoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dispositivoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
