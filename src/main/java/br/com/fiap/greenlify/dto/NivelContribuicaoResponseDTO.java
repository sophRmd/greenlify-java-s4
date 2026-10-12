package br.com.fiap.greenlify.dto;

public record NivelContribuicaoResponseDTO(
        int nivel,
        String nome,
        int xpMinimo,
        int xpMaximo
) {
}
