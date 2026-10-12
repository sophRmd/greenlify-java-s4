package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.PosicaoRankingIndividualRequestDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingIndividualResponseDTO;
import br.com.fiap.greenlify.models.PosicaoRanking;

public class PosicaoRankingIndividualMapper {
    public static PosicaoRanking dtoToEntity (PosicaoRankingIndividualRequestDTO dto){
        PosicaoRanking posicaoRanking = new PosicaoRanking();
        posicaoRanking.setPosicao(dto.posicao());
        posicaoRanking.setPontuacao(dto.pontuacao());
        posicaoRanking.setUsuario(dto.usuario());
        posicaoRanking.setRanking(dto.ranking());
        return posicaoRanking;
    }

    public static PosicaoRankingIndividualResponseDTO toDto (PosicaoRanking posicaoRanking){
        PosicaoRankingIndividualResponseDTO dto = new PosicaoRankingIndividualResponseDTO(
                posicaoRanking.getPosicao(),
                posicaoRanking.getPontuacao(),
                posicaoRanking.getUsuario(),
                posicaoRanking.getRanking()
        );
        return dto;
    }
}