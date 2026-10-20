package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.RankingRequestDTO;
import br.com.fiap.greenlify.dto.RankingResponseDTO;
import br.com.fiap.greenlify.service.RankingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/rankings")
public class RankingController {
    private final RankingService service;

    public RankingController() {
        this.service = new RankingService();
    }

    @PostMapping
    public ResponseEntity<String> cadastrar(@RequestBody RankingRequestDTO ranking) {
        try {
            service.cadastrar(ranking);
            return ResponseEntity.status(HttpStatus.CREATED).body("Ranking cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar o ranking: " + e.getMessage());
        }
    }

    /** Recalcula os rankings do mês e o all time. Só administrador: cabeçalhos X-Admin-Email e X-Admin-Senha. */
    @PostMapping("/atualizar")
    public ResponseEntity<String> atualizar(
            @RequestHeader(value = "X-Admin-Email", required = false) String email,
            @RequestHeader(value = "X-Admin-Senha", required = false) String senha) {
        try {
            service.exigirAdministrador(email, senha);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

        try {
            service.atualizarRankings();
            return ResponseEntity.ok("Rankings atualizados com sucesso!");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar os rankings: " + e.getMessage());
        }
    }

    /**
     * Fecha o ranking individual (já encerrado) e concede as recompensas. Fechamento manual,
     * só administrador: cabeçalhos X-Admin-Email e X-Admin-Senha. Pode ser repetido sem duplicar prêmios.
     */
    @PostMapping("/{id}/fechar")
    public ResponseEntity<String> fechar(
            @PathVariable int id,
            @RequestHeader(value = "X-Admin-Email", required = false) String email,
            @RequestHeader(value = "X-Admin-Senha", required = false) String senha) {
        try {
            service.exigirAdministrador(email, senha);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }

        try {
            RankingService.ResultadoFechamento r = service.fecharRanking(id);
            return ResponseEntity.ok("Ranking fechado! Recompensas concedidas: " + r.concedidas()
                    + ", já concedidas antes: " + r.jaConcedidas()
                    + ", não concedidas: " + r.naoConcedidas() + ".");
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao fechar o ranking: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RankingResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RankingResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> alterar(@PathVariable int id,
                                          @RequestBody RankingRequestDTO objeto) {
        try {
            service.alterar(objeto, id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("não encontrado")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao alterar o ranking: " + e.getMessage());
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