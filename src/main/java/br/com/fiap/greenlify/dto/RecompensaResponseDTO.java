package br.com.fiap.greenlify.dto;

public record RecompensaResponseDTO(
        int bonusPercentual,
        int bonusPontos,
        String descricao
) {
}