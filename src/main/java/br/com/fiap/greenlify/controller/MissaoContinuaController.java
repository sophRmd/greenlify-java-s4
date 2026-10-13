package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.MissaoContinuaCompletaRequestDTO;
import br.com.fiap.greenlify.dto.MissaoContinuaCompletaResponseDTO;
import br.com.fiap.greenlify.service.MissaoContinuaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/missoes-continuas")
public class MissaoContinuaController {

    private MissaoContinuaService service = new MissaoContinuaService();

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
    public ResponseEntity<String> cadastrar(@RequestBody MissaoContinuaCompletaRequestDTO dto) {
        try {
            service.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Missão contínua cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar a missão contínua: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id,
                                          @RequestBody MissaoContinuaCompletaRequestDTO dto) {
        try {
            service.atualizar(id, dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
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