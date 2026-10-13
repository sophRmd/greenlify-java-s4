package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.UsuarioMissaoRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioMissaoResponseDTO;
import br.com.fiap.greenlify.service.UsuarioMissaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/usuarios-missoes")
public class UsuarioMissaoController {
    private final UsuarioMissaoService service;

    public UsuarioMissaoController() {
        this.service = new UsuarioMissaoService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody UsuarioMissaoRequestDTO usuarioMissao) {
        try {
            service.cadastrar(usuarioMissao);
            return ResponseEntity.status(HttpStatus.CREATED).body("Missão atribuída ao usuário com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao atribuir a missão ao usuário: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioMissaoResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioMissaoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<UsuarioMissaoResponseDTO>> listarPorUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody UsuarioMissaoRequestDTO objeto) {
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar o registro: " + e.getMessage());
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