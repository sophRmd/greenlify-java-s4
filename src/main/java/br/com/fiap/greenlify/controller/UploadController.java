package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.UploadRequestDTO;
import br.com.fiap.greenlify.dto.UploadResponseDTO;
import br.com.fiap.greenlify.service.UploadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Erro ao cadastrar o upload: " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao cadastrar o upload: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UploadResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UploadResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
