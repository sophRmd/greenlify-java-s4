package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.RankingAllTimeResponseDTO;
import br.com.fiap.greenlify.service.RankingAllTimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/rankings/all-time")
public class RankingAllTimeController {
    private final RankingAllTimeService service;

    public RankingAllTimeController() {
        this.service = new RankingAllTimeService();
    }

    @GetMapping
    public ResponseEntity<List<RankingAllTimeResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
