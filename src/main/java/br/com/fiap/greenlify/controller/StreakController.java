package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.StreakRequestDTO;
import br.com.fiap.greenlify.dto.StreakResponseDTO;
import br.com.fiap.greenlify.service.StreakService;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<String> cadastrar(@PathVariable int idUsuario,
                                            @RequestBody StreakRequestDTO streak) {
        try {
            service.cadastrar(streak, idUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Sequência cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar a sequência: " + e.getMessage());
        }
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

    @PutMapping("/usuario/{idUsuario}")
    public ResponseEntity<String> atualizar(@PathVariable int idUsuario,
                                            @RequestBody StreakRequestDTO objeto) {
        try {
            service.atualizar(objeto, idUsuario);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao atualizar a sequência: " + e.getMessage());
        }
    }
}