package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.RankingRedisDAO;
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
    private final RankingRedisDAO redisDAO;

    public RankingAllTimeService() {
        this.usuarioMissaoDAO = new UsuarioMissaoDAO();
        this.usuarioDAO = new UsuarioComumDAO();
        this.redisDAO = new RankingRedisDAO();
    }

    /**
     * Lista o ranking all time, do usuário com mais missões concluídas para o
     * com menos (empate: o cadastro mais antigo vem primeiro).
     */
    public List<RankingAllTimeResponseDTO> listar() {
        // Redis (ZREVRANGE WITHSCORES); se estiver fora do ar ou vazio, usa o Oracle e popula o Redis.
        Map<Integer, Integer> totais = redisDAO.listar();
        if (totais == null || totais.isEmpty()) {
            totais = usuarioMissaoDAO.contarConcluidasPorUsuario();
            if (totais.isEmpty() == false) {
                redisDAO.reconstruir(totais);
            }
        }
        final Map<Integer, Integer> pontos = totais;

        List<UsuarioComum> usuarios = usuarioDAO.listar();
        List<RankingAllTimeResponseDTO> ranking = new ArrayList<>();
        if (usuarios == null) {
            return ranking;
        }

        usuarios.sort(Comparator
                .comparingInt((UsuarioComum u) -> pontos.getOrDefault(u.getIdUsuario(), 0)).reversed()
                .thenComparingInt(UsuarioComum::getIdUsuario));

        int posicao = 1;
        for (UsuarioComum usuario : usuarios) {
            ranking.add(new RankingAllTimeResponseDTO(
                    posicao++,
                    UsuarioResumoMapper.toDto(usuario),
                    pontos.getOrDefault(usuario.getIdUsuario(), 0)));
        }
        return ranking;
    }

    /** Reconstrói o Sorted Set a partir do Oracle (usado ao atualizar os rankings). */
    public void reconstruir() {
        redisDAO.reconstruir(usuarioMissaoDAO.contarConcluidasPorUsuario());
    }

    /**
     * Chamado uma vez cada vez que um usuário conclui uma missão (conclusão já validada e
     * gravada): soma no ranking geral (all time) e atualiza o ranking individual do mês.
     * Os rankings são derivados do que já está no banco, então uma falha aqui não desfaz a
     * conclusão: é só registrada, e a próxima atualização dos rankings corrige tudo.
     */
    public void registrarMissaoConcluida(int idUsuario) {
        redisDAO.incrementar(idUsuario); // ZINCRBY ranking:all-time 1 {idUsuario}

        try {
            new RankingService().atualizarRankingMensalIndividual();
        } catch (RuntimeException e) {
            System.err.println("Não foi possível atualizar o ranking do mês: " + e.getMessage());
        }
    }
}