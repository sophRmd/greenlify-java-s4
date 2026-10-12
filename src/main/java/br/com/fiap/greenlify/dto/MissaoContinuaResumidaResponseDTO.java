package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.enums.TipoMetricaEnum;
import br.com.fiap.greenlify.models.Pontuacao;

public record MissaoContinuaResumidaResponseDTO(
        String nome,
        TipoMetricaEnum tipoMetrica,
        int valorMetrica,
        Pontuacao pontuacao
) {
}
