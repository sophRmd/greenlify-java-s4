package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.Grupo;
import br.com.fiap.greenlify.models.Ranking;
import br.com.fiap.greenlify.models.UsuarioComum;

public record PosicaoRankingGrupoRequestDTO(
        int posicao,
        int pontuacao,
        Grupo grupo,
        Ranking ranking
) {
}
