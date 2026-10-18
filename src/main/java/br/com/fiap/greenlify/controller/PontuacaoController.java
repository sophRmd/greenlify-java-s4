package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.PontuacaoRequestDTO;
import br.com.fiap.greenlify.dto.PontuacaoResponseDTO;
import br.com.fiap.greenlify.service.PontuacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/pontuacoes")
public class PontuacaoController {
    private final PontuacaoService service;

    public PontuacaoController() {
        this.service = new PontuacaoService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody PontuacaoRequestDTO pontuacao) {
        try {
            service.cadastrar(pontuacao);
            return ResponseEntity.status(HttpStatus.CREATED).body("Pontuação cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar a pontuação: " + e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<PontuacaoResponseDTO> buscarPorImpactoEDificuldade(
            @RequestParam String impacto,
            @RequestParam String dificuldade) {
        try {
            return ResponseEntity.ok(service.buscarPorImpactoEDificuldade(impacto, dificuldade));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<PontuacaoResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody PontuacaoRequestDTO objeto) {
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar a pontuação: " + e.getMessage());
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