package com.ecoguardia.controller;

import com.ecoguardia.model.Ubicacion;
import com.ecoguardia.repository.UbicacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CRUD de aulas/ubicaciones para la vista "Gestion de aulas".
 */
@RestController
@RequestMapping("/api/ubicaciones")
public class UbicacionController {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionController(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @GetMapping
    public List<Ubicacion> listar() {
        return ubicacionRepository.findAll();
    }

    @PostMapping
    public Ubicacion crear(@RequestBody Ubicacion ubicacion) {
        return ubicacionRepository.save(ubicacion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ubicacion> actualizar(
            @PathVariable Long id, @RequestBody Ubicacion datos) {
        return ubicacionRepository.findById(id)
                .map(u -> {
                    u.setNombre(datos.getNombre());
                    u.setDescripcion(datos.getDescripcion());
                    return ResponseEntity.ok(ubicacionRepository.save(u));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (!ubicacionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ubicacionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
