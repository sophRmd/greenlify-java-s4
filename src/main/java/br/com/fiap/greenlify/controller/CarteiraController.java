package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.CarteiraRequestDTO;
import br.com.fiap.greenlify.dto.CarteiraResponseDTO;
import br.com.fiap.greenlify.service.CarteiraService;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/carteira")
public class CarteiraController {
    private final UsuarioService usuarioService = new UsuarioService();

    private CarteiraService service = new CarteiraService();

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CarteiraResponseDTO> buscarPorIdUsuario(
            @PathVariable int idUsuario,
            @RequestHeader(value = "X-Usuario-Email", required = false) String email,
            @RequestHeader(value = "X-Usuario-Senha", required = false) String senha) {
        usuarioService.exigirDono(email, senha, idUsuario);
        try {
            return ResponseEntity.ok(service.buscarPorIdUsuario(idUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<String> cadastrar(
            @PathVariable int idUsuario,
            @RequestBody CarteiraRequestDTO dto,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.cadastrar(idUsuario, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Carteira cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar a carteira: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable int id,
            @RequestBody CarteiraRequestDTO dto,
            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.atualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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