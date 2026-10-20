package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.UsuarioCadastroRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioCadastroResponseDTO;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public record LoginRequest(String email, String senha) {}

    public UsuarioController() {
        this.service = new UsuarioService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody UsuarioCadastroRequestDTO usuario) {
        try {
            service.cadastrar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar o usuário: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioCadastroResponseDTO> login(@RequestBody LoginRequest login) {
        try {
            return ResponseEntity.ok(service.login(login.email(), login.senha()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioCadastroResponseDTO> buscarPorId(
            @PathVariable int id,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        service.exigirDono(email, senha, id);
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioCadastroResponseDTO>> listar(
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        service.exigirAdministrador(adminEmail, adminSenha);
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody UsuarioCadastroRequestDTO objeto,
                                          @RequestHeader(value = "X-Usuario-Email", required = false) String email,
                                          @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        service.exigirDono(email, senha, id);
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar o usuário: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable int id,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        service.exigirDono(email, senha, id);
        try {
            service.excluir(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}