package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.ContaLuzRequestDTO;
import br.com.fiap.greenlify.dto.ContalLuzResponseDTO;
import br.com.fiap.greenlify.service.ContaLuzService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/contas-luz")
public class ContaLuzController {

    private ContaLuzService service = new ContaLuzService();

    @GetMapping
    public ResponseEntity<List<ContalLuzResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContalLuzResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody ContaLuzRequestDTO dto) {
        try {
            service.cadastrar(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Conta de luz cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar a conta de luz: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id,
                                          @RequestBody ContaLuzRequestDTO dto) {
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