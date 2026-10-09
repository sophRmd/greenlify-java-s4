package br.com.fiap.greenlify.dto;

import java.time.LocalDate;

public record GrupoResponseDTO(
        String nome,
        LocalDate dataCriacao,
        int qtdUsuarios
) {
}
