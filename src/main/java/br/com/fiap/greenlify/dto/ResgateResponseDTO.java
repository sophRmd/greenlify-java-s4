package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.Recompensa;
import br.com.fiap.greenlify.models.Usuario;

import java.time.LocalDate;

public record ResgateResponseDTO(
        int idResgate,
        LocalDate dataConcessao,
        Usuario usuario,
        Recompensa recompensa
) {
}