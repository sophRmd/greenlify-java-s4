package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.MissaoMensalCompletaRequestDTO;
import br.com.fiap.greenlify.dto.MissaoMensalCompletaResponseDTO;
import br.com.fiap.greenlify.service.MissaoMensalService;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/missoes-mensais")
public class MissaoMensalController {

    private MissaoMensalService service = new MissaoMensalService();
    private final UsuarioService usuarioService = new UsuarioService();

    @GetMapping
    public ResponseEntity<List<MissaoMensalCompletaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissaoMensalCompletaResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody MissaoMensalCompletaRequestDTO dto,
                                            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
                                            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Missão mensal cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar a missão mensal: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id,
                                          @RequestBody MissaoMensalCompletaRequestDTO dto,
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
    public ResponseEntity<Void> excluir(@PathVariable int id,
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