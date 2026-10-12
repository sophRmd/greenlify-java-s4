package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.Recompensa;
import br.com.fiap.greenlify.models.Usuario;

import java.time.LocalDate;

public record ResgateRequestDTO(
        LocalDate dataConcessao,
        Usuario usuario,
        Recompensa recompensa
) {
}