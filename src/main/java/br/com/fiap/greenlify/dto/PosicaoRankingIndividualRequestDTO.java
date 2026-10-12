package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.Ranking;
import br.com.fiap.greenlify.models.UsuarioComum;

public record PosicaoRankingIndividualRequestDTO(
        int posicao,
        int pontuacao,
        UsuarioComum usuario,
        Ranking ranking
) {
}