package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.UploadRequestDTO;
import br.com.fiap.greenlify.dto.UploadResponseDTO;
import br.com.fiap.greenlify.service.UploadService;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/uploads")
public class UploadController {
    private final UploadService service;
    private final UsuarioService usuarioService;

    public UploadController() {
        this.service = new UploadService();
        this.usuarioService = new UsuarioService();
    }

    /** Devolve o upload gravado, já com status, parecer e confiança de duplicidade. */
    @PostMapping
    public ResponseEntity<?> cadastrar(
            @RequestBody UploadRequestDTO upload,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        // Exige login e que o envio seja em nome do próprio usuário autenticado.
        usuarioService.exigirDono(email, senha, upload.idUsuario());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(upload));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar o upload: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar o upload: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UploadResponseDTO> buscarPorId(
            @PathVariable int id,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        int idAutenticado = usuarioService.autenticar(email, senha);
        try {
            UploadResponseDTO upload = service.buscarPorId(id);
            if (upload.usuario() == null || upload.usuario().idUsuario() != idAutenticado) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();   // upload de outro usuário
            }
            return ResponseEntity.ok(upload);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UploadResponseDTO>> listar(
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        return ResponseEntity.ok(service.listar());
    }
}