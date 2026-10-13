package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.StreakDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.StreakRequestDTO;
import br.com.fiap.greenlify.dto.StreakResponseDTO;
import br.com.fiap.greenlify.mapper.StreakMapper;
import br.com.fiap.greenlify.models.Streak;
import org.springframework.stereotype.Service;

@Service
public class StreakService {
    private final StreakDAO streakDAO;
    private final UsuarioComumDAO usuarioDAO;

    public StreakService() {
        this.streakDAO = new StreakDAO();
        this.usuarioDAO = new UsuarioComumDAO();
    }

    public StreakResponseDTO buscarPorIdUsuario(int idUsuario) {
        Streak streak = streakDAO.buscarPorIdUsuario(idUsuario);
        if (streak == null) {
            throw new IllegalArgumentException("Sequência não encontrada");
        }
        return StreakMapper.toDto(streak);
    }

    /**
     * Retorna a sequência do usuário, criando uma zerada caso ainda não exista.
     */
    public StreakResponseDTO buscarOuCriar(int idUsuario) {
        verificarUsuario(idUsuario);
        return StreakMapper.toDto(streakDAO.buscarOuCriar(idUsuario));
    }

    public void cadastrar(StreakRequestDTO dto, int idUsuario) {
        verificarUsuario(idUsuario);
        validar(dto);

        if (streakDAO.buscarPorIdUsuario(idUsuario) != null) {
            System.out.println("ERRO: Já existe uma sequência para esse usuário.");
            throw new IllegalArgumentException("Já existe uma sequência para esse usuário.");
        }

        Streak streak = StreakMapper.dtoToEntity(dto);
        streak.setIdStreak(idUsuario);
        streakDAO.cadastrar(streak, idUsuario);
    }

    public void atualizar(StreakRequestDTO dto, int idUsuario) {
        if (streakDAO.buscarPorIdUsuario(idUsuario) == null) {
            throw new IllegalArgumentException("Sequência não encontrada");
        }
        validar(dto);

        Streak streak = StreakMapper.dtoToEntity(dto);
        streak.setIdStreak(idUsuario);
        streakDAO.atualizar(streak, idUsuario);
    }

    private void verificarUsuario(int idUsuario) {
        if (usuarioDAO.buscarPorId(idUsuario) == null) {
            System.out.println("ERRO: Usuário informado não existe.");
            throw new IllegalArgumentException("O usuário informado não existe.");
        }
    }

    private void validar(StreakRequestDTO dto) {
        if (dto.maiorSequencia() < 0) {
            System.out.println("ERRO: A maior sequência não pode ser negativa.");
            throw new IllegalArgumentException("A maior sequência não pode ser negativa.");
        }
        if (dto.sequenciaAtual() < 0) {
            System.out.println("ERRO: A sequência atual não pode ser negativa.");
            throw new IllegalArgumentException("A sequência atual não pode ser negativa.");
        }
        if (dto.sequenciaAtual() > dto.maiorSequencia()) {
            System.out.println("ERRO: A sequência atual não pode ser maior que a maior sequência.");
            throw new IllegalArgumentException(
                    "A sequência atual não pode ser maior que a maior sequência.");
        }
        if (dto.primeiraAtividade() != null
                && dto.ultimaAtividade() != null
                && dto.ultimaAtividade().isBefore(dto.primeiraAtividade())) {
            System.out.println("ERRO: A última atividade não pode ser anterior à primeira.");
            throw new IllegalArgumentException(
                    "A última atividade não pode ser anterior à primeira.");
        }
    }
}