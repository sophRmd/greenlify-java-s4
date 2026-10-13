package br.com.fiap.greenlify.controller;

import br.com.fiap.greenlify.dto.CarteiraRequestDTO;
import br.com.fiap.greenlify.dto.CarteiraResponseDTO;
import br.com.fiap.greenlify.service.CarteiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/carteira")
public class CarteiraController {

    private CarteiraService service = new CarteiraService();

    @GetMapping
    public ResponseEntity<List<CarteiraResponseDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarteiraResponseDTO> buscarPorId(@PathVariable int id) {
        try {
            return ResponseEntity.ok(service.buscarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<CarteiraResponseDTO> buscarPorIdUsuario(@PathVariable int idUsuario) {
        try {
            return ResponseEntity.ok(service.buscarPorIdUsuario(idUsuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/usuario/{idUsuario}")
    public ResponseEntity<String> cadastrar(@PathVariable int idUsuario,
                                            @RequestBody CarteiraRequestDTO dto) {
        try {
            service.cadastrar(idUsuario, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Carteira cadastrada com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao cadastrar a carteira: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id,
                                          @RequestBody CarteiraRequestDTO dto) {
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