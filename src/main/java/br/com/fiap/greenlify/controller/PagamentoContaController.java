package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.PagamentoContaRequestDTO;
import br.com.fiap.greenlify.dto.PagamentoContaResponseDTO;
import br.com.fiap.greenlify.service.PagamentoContaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/pagamentos")
public class PagamentoContaController {
    private final PagamentoContaService service;

    public PagamentoContaController() {
        this.service = new PagamentoContaService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody PagamentoContaRequestDTO pagamento) {
        try {
            service.cadastrar(pagamento);
            return ResponseEntity.status(HttpStatus.CREATED).body("Pagamento cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar o pagamento: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoContaResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PagamentoContaResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PagamentoContaResponseDTO>> listarPorUsuario(@PathVariable int idUsuario) {
        return ResponseEntity.ok(service.listarPorUsuario(idUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody PagamentoContaRequestDTO objeto) {
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar o pagamento: " + e.getMessage());
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