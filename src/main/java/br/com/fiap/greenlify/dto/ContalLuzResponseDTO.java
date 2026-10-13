package br.com.fiap.greenlify.dto;

import java.time.LocalDate;

public record ContalLuzResponseDTO(
        String codigoBarras,
        double valorConta
) {
}
