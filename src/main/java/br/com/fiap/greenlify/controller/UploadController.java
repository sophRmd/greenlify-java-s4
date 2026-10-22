package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.UploadRequestDTO;
import br.com.fiap.greenlify.dto.UploadResponseDTO;
import br.com.fiap.greenlify.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Envio de comprovações. Os endpoints não exigem login. Um upload gravado não tem alteração nem
 * exclusão: o resultado da análise e a pontuação já consolidada ficam preservados.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/uploads")
public class UploadController {
    private final UploadService service;

    public UploadController() {
        this.service = new UploadService();
    }

    /** Devolve o upload gravado, já com status, parecer e confiança de duplicidade. */
    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody UploadRequestDTO upload) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.cadastrar(upload));
        } catch (IllegalArgumentException e) {
            return erro(e, "Erro ao cadastrar o upload");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar o upload: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            // na busca por id, qualquer recusa significa que o upload não existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<UploadResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    /** Recurso inexistente vira 404; qualquer outra falha de validação vira 400. */
    private ResponseEntity<String> erro(IllegalArgumentException e, String contexto) {
        String mensagem = e.getMessage();
        if (mensagem != null && mensagem.contains("não encontrad")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mensagem);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(contexto + ": " + mensagem);
    }
}