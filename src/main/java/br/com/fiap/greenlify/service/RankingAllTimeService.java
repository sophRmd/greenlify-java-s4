package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dao.UsuarioMissaoDAO;
import br.com.fiap.greenlify.dto.RankingAllTimeResponseDTO;
import br.com.fiap.greenlify.mapper.UsuarioResumoMapper;
import br.com.fiap.greenlify.models.UsuarioComum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Ranking all time: cada usuário vale o total de missões que já concluiu
 * (não depende da carteira, então pagar uma conta não faz ninguém cair).
 *
 * <p>Não é gravado em Ranking/Posicao_Ranking nem recalculado pelo agendamento
 * do {@link RankingService}: ele não tem período e deve ser atualizado a cada
 * missão concluída. Por isso existem os dois pontos de troca para o Redis
 * sorted set, marcados com TODO.</p>
 */
@Service
public class RankingAllTimeService {
    private final UsuarioMissaoDAO usuarioMissaoDAO;
    private final UsuarioComumDAO usuarioDAO;

    public RankingAllTimeService() {
        this.usuarioMissaoDAO = new UsuarioMissaoDAO();
        this.usuarioDAO = new UsuarioComumDAO();
    }

    /**
     * Lista o ranking all time, do usuário com mais missões concluídas para o
     * com menos (empate: o cadastro mais antigo vem primeiro).
     */
    public List<RankingAllTimeResponseDTO> listar() {
        // TODO(Redis): trocar por ZREVRANGE ranking:all-time 0 -1 WITHSCORES.
        // Esta consulta agrupada passa a servir só para reconstruir o sorted set.
        Map<Integer, Integer> totais = usuarioMissaoDAO.contarConcluidasPorUsuario();

        List<UsuarioComum> usuarios = usuarioDAO.listar();
        List<RankingAllTimeResponseDTO> ranking = new ArrayList<>();
        if (usuarios == null) {
            return ranking;
        }

        usuarios.sort(Comparator
                .comparingInt((UsuarioComum u) -> totais.getOrDefault(u.getIdUsuario(), 0)).reversed()
                .thenComparingInt(UsuarioComum::getIdUsuario));

        int posicao = 1;
        for (UsuarioComum usuario : usuarios) {
            ranking.add(new RankingAllTimeResponseDTO(
                    posicao++,
                    UsuarioResumoMapper.toDto(usuario),
                    totais.getOrDefault(usuario.getIdUsuario(), 0)));
        }
        return ranking;
    }

    /**
     * Chamado uma vez cada vez que um usuário conclui uma missão.
     */
    public void registrarMissaoConcluida(int idUsuario) {
        // TODO(Redis): ZINCRBY ranking:all-time 1 {idUsuario}
    }
}
