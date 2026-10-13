package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.PosicaoRankingGrupoRequestDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingGrupoResponseDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingIndividualRequestDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingIndividualResponseDTO;
import br.com.fiap.greenlify.service.PosicaoRankingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/posicoes-ranking")
public class PosicaoRankingController {
    private final PosicaoRankingService service;

    public PosicaoRankingController() {
        this.service = new PosicaoRankingService();
    }

    // ---------- Grupo ----------

    @PostMapping("/grupo")
    public ResponseEntity<String> cadastrarGrupo(@RequestBody PosicaoRankingGrupoRequestDTO posicao) {
        try {
            service.cadastrarGrupo(posicao);
            return ResponseEntity.status(HttpStatus.CREATED).body("Posição de grupo cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar a posição de grupo: " + e.getMessage());
        }
    }

    @GetMapping("/grupo")
    public ResponseEntity<List<PosicaoRankingGrupoResponseDTO>> listarGrupos() {
        return ResponseEntity.ok(service.listarGrupos());
    }

    @GetMapping("/grupo/{id}")
    public ResponseEntity<PosicaoRankingGrupoResponseDTO> buscarGrupoPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarGrupoPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/grupo/{id}")
    public ResponseEntity<String> alterarGrupo(@PathVariable int id,
                                               @RequestBody PosicaoRankingGrupoRequestDTO objeto) {
        try {
            service.alterarGrupo(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar a posição de grupo: " + e.getMessage());
        }
    }

    // ---------- Individual ----------

    @PostMapping("/individual")
    public ResponseEntity<String> cadastrarIndividual(@RequestBody PosicaoRankingIndividualRequestDTO posicao) {
        try {
            service.cadastrarIndividual(posicao);
            return ResponseEntity.status(HttpStatus.CREATED).body("Posição individual cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar a posição individual: " + e.getMessage());
        }
    }

    @GetMapping("/individual")
    public ResponseEntity<List<PosicaoRankingIndividualResponseDTO>> listarIndividuais() {
        return ResponseEntity.ok(service.listarIndividuais());
    }

    @GetMapping("/individual/{id}")
    public ResponseEntity<PosicaoRankingIndividualResponseDTO> buscarIndividualPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarIndividualPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/individual/{id}")
    public ResponseEntity<String> alterarIndividual(@PathVariable int id,
                                                    @RequestBody PosicaoRankingIndividualRequestDTO objeto) {
        try {
            service.alterarIndividual(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrada")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar a posição individual: " + e.getMessage());
        }
    }

    // ---------- Comum ----------

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