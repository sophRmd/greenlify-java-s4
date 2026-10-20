package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.MissaoContinuaCompletaRequestDTO;
import br.com.fiap.greenlify.dto.MissaoContinuaCompletaResponseDTO;
import br.com.fiap.greenlify.service.MissaoContinuaService;
import br.com.fiap.greenlify.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/missoes-continuas")
public class MissaoContinuaController {

    private MissaoContinuaService service = new MissaoContinuaService();
    private final UsuarioService usuarioService = new UsuarioService();

    @GetMapping
    public ResponseEntity<List<MissaoContinuaCompletaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissaoContinuaCompletaResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody MissaoContinuaCompletaRequestDTO dto,
                                            @RequestHeader(value = "X-Admin-Email", required = false) String adminEmail,
                                            @RequestHeader(value = "X-Admin-Senha", required = false) String adminSenha) {
        usuarioService.exigirAdministrador(adminEmail, adminSenha);
        try {
            service.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Missão contínua cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar a missão contínua: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id,
                                          @RequestBody MissaoContinuaCompletaRequestDTO dto,
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