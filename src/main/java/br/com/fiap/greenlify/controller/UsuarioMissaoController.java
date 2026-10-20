package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.UsuarioMissaoRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioMissaoResponseDTO;
import br.com.fiap.greenlify.service.UsuarioMissaoService;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/usuarios-missoes")
public class UsuarioMissaoController {
    private final UsuarioMissaoService service;
    private final UsuarioService usuarioService;

    public UsuarioMissaoController() {
        this.service = new UsuarioMissaoService();
        this.usuarioService = new UsuarioService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(
            @RequestBody UsuarioMissaoRequestDTO usuarioMissao,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        int idUsuario = usuarioService.autenticar(email, senha);   // o usuário é o autenticado
        try {
            service.cadastrar(usuarioMissao, idUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Missão atribuída ao usuário com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao atribuir a missão ao usuário: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioMissaoResponseDTO>> listar(
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<UsuarioMissaoResponseDTO>> listarPorUsuario(
            @PathVariable int idUsuario,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        usuarioService.exigirDono(email, senha, idUsuario);
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    /**
     * Registra um passo de progresso na missão mensal. O cliente não informa o progresso: o
     * servidor avança um passo e decide quando a missão foi concluída.
     */
    @PostMapping("/{id}/progresso")
    public ResponseEntity<?> registrarProgresso(
            @PathVariable int id,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        int idUsuario = usuarioService.autenticar(email, senha);
        try {
            return ResponseEntity.ok(service.registrarProgresso(id, idUsuario));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao registrar o progresso: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao registrar o progresso: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable int id,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.excluir(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}