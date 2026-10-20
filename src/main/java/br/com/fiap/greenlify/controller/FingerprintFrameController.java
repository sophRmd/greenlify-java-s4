package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.FingerprintFrameRequestDTO;
import br.com.fiap.greenlify.dto.FingerprintFrameResponseDTO;
import br.com.fiap.greenlify.service.FingerprintFrameService;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/fingerprint-frames")
public class FingerprintFrameController {
    private final UsuarioService usuarioService = new UsuarioService();
    private final FingerprintFrameService service;

    public FingerprintFrameController() {
        this.service = new FingerprintFrameService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(
            @RequestBody FingerprintFrameRequestDTO frame,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.cadastrar(frame);
            return ResponseEntity.status(HttpStatus.CREATED).body("Frame cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar o frame: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<FingerprintFrameResponseDTO> buscarPorId(
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
    public ResponseEntity<List<FingerprintFrameResponseDTO>> listar(
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/fingerprint/{idFingerprint}")
    public ResponseEntity<List<FingerprintFrameResponseDTO>> listarPorFingerprint(
            @PathVariable int idFingerprint,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        return ResponseEntity.ok(service.listarPorFingerprint(idFingerprint));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(
            @PathVariable int id,
            @RequestBody FingerprintFrameRequestDTO objeto,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar o frame: " + e.getMessage());
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