package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.StreakResponseDTO;
import br.com.fiap.greenlify.service.StreakService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/streaks")
public class StreakController {
    private final StreakService service;

    public StreakController() {
        this.service = new StreakService();
    }

    @PostMapping("/usuario/{idUsuario}/iniciar")
    public ResponseEntity<StreakResponseDTO> buscarOuCriar(@PathVariable int idUsuario) {
        try {
            return ResponseEntity.ok(service.buscarOuCriar(idUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<StreakResponseDTO> buscarPorIdUsuario(@PathVariable int idUsuario) {
        try {
            return ResponseEntity.ok(service.buscarPorIdUsuario(idUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
