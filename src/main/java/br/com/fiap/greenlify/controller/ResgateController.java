package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.ResgateRequestDTO;
import br.com.fiap.greenlify.dto.ResgateResponseDTO;
import br.com.fiap.greenlify.service.ResgateService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/resgates")
public class ResgateController {
    private final ResgateService service;

    public ResgateController() {
        this.service = new ResgateService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody ResgateRequestDTO resgate) {
        try {
            service.cadastrar(resgate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Resgate cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar o resgate: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResgateResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ResgateResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody ResgateRequestDTO objeto) {
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar o resgate: " + e.getMessage());
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