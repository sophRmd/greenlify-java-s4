package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.FingerprintRequestDTO;
import br.com.fiap.greenlify.dto.FingerprintResponseDTO;
import br.com.fiap.greenlify.service.FingerprintService;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/fingerprints")
public class FingerprintController {
    private final UsuarioService usuarioService = new UsuarioService();
    private final FingerprintService service;

    public FingerprintController() {
        this.service = new FingerprintService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(
            @RequestBody FingerprintRequestDTO fingerprint,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.cadastrar(fingerprint);
            return ResponseEntity.status(HttpStatus.CREATED).body("Fingerprint cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar a fingerprint: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FingerprintResponseDTO> buscarPorId(
            @PathVariable int id,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FingerprintResponseDTO>> listar(
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(
            @PathVariable int id,
            @RequestBody FingerprintRequestDTO objeto,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar a fingerprint: " + e.getMessage());
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