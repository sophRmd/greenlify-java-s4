package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.Usuario;

import java.time.LocalDate;

public record StreakResponseDTO(
        int idStreak,
        int maiorSequencia,
        int sequenciaAtual,
        LocalDate ultimaAtividade,
        LocalDate primeiraAtividade,
        Usuario usuario
) {
}