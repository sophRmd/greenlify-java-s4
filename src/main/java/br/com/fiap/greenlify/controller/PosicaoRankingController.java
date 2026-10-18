package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.PosicaoRankingGrupoResponseDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingIndividualResponseDTO;
import br.com.fiap.greenlify.service.PosicaoRankingService;
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

    // ---------- Individual ----------

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


}
