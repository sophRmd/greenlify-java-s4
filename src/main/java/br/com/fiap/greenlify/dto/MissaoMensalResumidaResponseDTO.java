package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.enums.CategoriaEnum;

import java.time.LocalDate;

public record MissaoMensalResumidaResponseDTO(
        String nome,
        String descricao,
        int meta,
        LocalDate dataFim
) {
}
