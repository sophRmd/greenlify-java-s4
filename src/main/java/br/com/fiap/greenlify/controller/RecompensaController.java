package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.RecompensaRequestDTO;
import br.com.fiap.greenlify.dto.RecompensaResponseDTO;
import br.com.fiap.greenlify.service.RecompensaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/recompensas")
public class RecompensaController {
    private final RecompensaService service;

    public RecompensaController() {
        this.service = new RecompensaService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody RecompensaRequestDTO recompensa) {
        try {
            service.cadastrar(recompensa);
            return ResponseEntity.status(HttpStatus.CREATED).body("Recompensa cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar a recompensa: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecompensaResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RecompensaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody RecompensaRequestDTO objeto) {
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar a recompensa: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        try {
            service.excluir(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}