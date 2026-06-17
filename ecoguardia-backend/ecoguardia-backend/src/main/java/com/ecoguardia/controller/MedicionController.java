package com.ecoguardia.controller;

import com.ecoguardia.dto.MedicionRequest;
import com.ecoguardia.dto.MedicionResponse;
import com.ecoguardia.service.MedicionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de mediciones de ruido.
 *
 * POST /api/mediciones  -> lo usa el ESP32 para enviar datos
 * GET  /api/mediciones  -> lo usa el dashboard para cargar el historial reciente
 */
@RestController
@RequestMapping("/api/mediciones")
public class MedicionController {

    private final MedicionService medicionService;

    public MedicionController(MedicionService medicionService) {
        this.medicionService = medicionService;
    }

    /** El ESP32 envia aqui cada lectura. */
    @PostMapping
    public ResponseEntity<MedicionResponse> registrar(
            @Valid @RequestBody MedicionRequest request) {
        MedicionResponse response = medicionService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** El dashboard pide las ultimas mediciones al cargar. */
    @GetMapping
    public List<MedicionResponse> ultimas() {
        return medicionService.ultimas();
    }
}
