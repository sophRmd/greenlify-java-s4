package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.service.DadosDemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/dados-demo")
public class DadosDemoController {
    private final DadosDemoService service;

    public DadosDemoController() {
        this.service = new DadosDemoService();
    }

    /** Apaga os dados de movimento e recarrega os dados de demonstração. */
    @PostMapping("/restaurar")
    public ResponseEntity<String> restaurar() {
        if (!service.estaHabilitado()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Restauração desabilitada. Defina SEED_HABILITADO=true no ambiente para liberar.");
        }
        try {
            return ResponseEntity.ok(service.restaurar());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao restaurar os dados de demonstração: " + e.getMessage());
        }
    }
}