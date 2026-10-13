package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.UsuarioSeloRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioSeloResponseDTO;
import br.com.fiap.greenlify.service.UsuarioSeloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/usuarios-selos")
public class UsuarioSeloController {
    private final UsuarioSeloService service;

    public UsuarioSeloController() {
        this.service = new UsuarioSeloService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody UsuarioSeloRequestDTO usuarioSelo) {
        try {
            service.cadastrar(usuarioSelo);
            return ResponseEntity.status(HttpStatus.CREATED).body("Coleta de selo registrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao registrar a coleta de selo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioSeloResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioSeloResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<UsuarioSeloResponseDTO>> listarPorUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody UsuarioSeloRequestDTO objeto) {
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar a coleta de selo: " + e.getMessage());
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