package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.Selo;
import br.com.fiap.greenlify.models.Usuario;

import java.time.LocalDate;

public record UsuarioSeloRequestDTO(
        LocalDate dataColeta,
        Usuario usuario,
        Selo selo
) {
}