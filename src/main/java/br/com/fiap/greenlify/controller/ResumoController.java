package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.ResumoGrupoResponseDTO;
import br.com.fiap.greenlify.dto.ResumoImpactoResponseDTO;
import br.com.fiap.greenlify.dto.ResumoMissoesResponseDTO;
import br.com.fiap.greenlify.service.ResumoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/resumos")
public class ResumoController {
    private final ResumoService service;

    public ResumoController() {
        this.service = new ResumoService();
    }

    @GetMapping("/missoes/{idUsuario}")
    public ResponseEntity<ResumoMissoesResponseDTO> resumirMissoes(@PathVariable int idUsuario) {
        try {
            return ResponseEntity.ok(service.resumirMissoes(idUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/grupos/{idGrupo}")
    public ResponseEntity<ResumoGrupoResponseDTO> resumirGrupo(@PathVariable int idGrupo) {
        try {
            return ResponseEntity.ok(service.resumirGrupo(idGrupo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/impacto/{idUsuario}")
    public ResponseEntity<ResumoImpactoResponseDTO> resumirImpacto(@PathVariable int idUsuario) {
        try {
            return ResponseEntity.ok(service.resumirImpacto(idUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
