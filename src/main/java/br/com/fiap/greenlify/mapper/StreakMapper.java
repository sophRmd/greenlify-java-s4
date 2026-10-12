package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.StreakRequestDTO;
import br.com.fiap.greenlify.dto.StreakResponseDTO;
import br.com.fiap.greenlify.models.Streak;

public class StreakMapper {
    public static Streak dtoToEntity (StreakRequestDTO dto){
        Streak streak = new Streak();
        streak.setIdStreak(dto.idStreak());
        streak.setMaiorSequencia(dto.maiorSequencia());
        streak.setSequenciaAtual(dto.sequenciaAtual());
        streak.setUltimaAtividade(dto.ultimaAtividade());
        streak.setPrimeiraAtividade(dto.primeiraAtividade());
        streak.setUsuario(dto.usuario());
        return streak;
    }

    public static StreakResponseDTO toDto (Streak streak){
        StreakResponseDTO dto = new StreakResponseDTO(
                streak.getIdStreak(),
                streak.getMaiorSequencia(),
                streak.getSequenciaAtual(),
                streak.getUltimaAtividade(),
                streak.getPrimeiraAtividade(),
                streak.getUsuario()
        );
        return dto;
    }
}