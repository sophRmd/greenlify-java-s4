package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.AdminDAO;
import br.com.fiap.greenlify.dao.PosicaoRankingDAO;
import br.com.fiap.greenlify.dao.RankingDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.RankingRequestDTO;
import br.com.fiap.greenlify.dto.RankingResponseDTO;
import br.com.fiap.greenlify.mapper.RankingMapper;
import br.com.fiap.greenlify.enums.StatusRankingEnum;
import br.com.fiap.greenlify.enums.TipoRankingEnum;
import br.com.fiap.greenlify.models.PosicaoRanking;
import br.com.fiap.greenlify.models.Ranking;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RankingService {
    private final RankingDAO rankingDAO;
    private final PosicaoRankingDAO posicaoDAO;

    /**
     * Trava única (vale para todas as instâncias do serviço) para quem regrava as posições do
     * ranking do mês: evita que duas atualizações simultâneas dupliquem as posições.
     */
    private static final Object TRAVA_POSICOES = new Object();

    public RankingService() {
        this.rankingDAO = new RankingDAO();
        this.posicaoDAO = new PosicaoRankingDAO();
    }

    /**
     * Recalcula os rankings (individual e de grupos) do mês atual: fecha os
     * períodos que já terminaram, cria o ranking do mês se ainda não existir e
     * regrava as posições usando as regras do model {@link Ranking}
     * (individual: missões concluídas no mês e, no empate, cadastro mais antigo;
     * grupos: total de selos dos integrantes) e reconstrói o ranking all time no Redis.
     *
     * @throws IllegalStateException se o banco não confirmar a criação do ranking
     */
    public void atualizarRankings() {
        synchronized (TRAVA_POSICOES) {
            atualizarRankingsSemTrava();
        }
    }

    /**
     * Atualiza só o ranking individual do mês corrente (cria o ranking do mês se ainda não
     * existir) e regrava as posições. É chamado a cada missão concluída, para o ranking
     * mensal acompanhar as conclusões sem esperar a atualização manual do administrador.
     */
    public void atualizarRankingMensalIndividual() {
        synchronized (TRAVA_POSICOES) {
            LocalDate hoje = LocalDate.now();
            Ranking atual = null;
            for (Ranking r : rankingDAO.listar()) {
                if (r.getTipo() == TipoRankingEnum.INDIVIDUAL && r.getStatus() == StatusRankingEnum.ABERTO
                        && r.getDataInicio() != null && r.getDataFim() != null
                        && !hoje.isBefore(r.getDataInicio()) && !hoje.isAfter(r.getDataFim())) {
                    atual = r;
                    break;
                }
            }

            if (atual == null) {
                atual = new Ranking();
                atual.setTipo(TipoRankingEnum.INDIVIDUAL);
                atual.setDataInicio(hoje.withDayOfMonth(1));
                atual.setDataFim(hoje.withDayOfMonth(hoje.lengthOfMonth()));
                atual.setStatus(StatusRankingEnum.ABERTO);
                rankingDAO.cadastrar(atual);
                if (atual.getIdRanking() <= 0) {
                    throw new IllegalStateException("Não foi possível criar o ranking do mês.");
                }
            }

            posicaoDAO.excluirPorRanking(atual.getIdRanking());
            for (PosicaoRanking posicao : atual.calcularRankingUsuarios()) {
                posicaoDAO.cadastrar(posicao);
            }
        }
    }

    private void atualizarRankingsSemTrava() {
        LocalDate hoje = LocalDate.now();
        List<Ranking> todos = rankingDAO.listar();

        for (Ranking r : todos) {
            if (r.getStatus() == StatusRankingEnum.ABERTO && r.getDataFim() != null
                    && r.getDataFim().isBefore(hoje)) {
                r.setStatus(StatusRankingEnum.FECHADO);
                rankingDAO.alterar(r);
            }
        }

        for (TipoRankingEnum tipo : TipoRankingEnum.values()) {
            Ranking atual = null;
            for (Ranking r : todos) {
                if (r.getTipo() == tipo && r.getStatus() == StatusRankingEnum.ABERTO
                        && r.getDataInicio() != null && r.getDataFim() != null
                        && !hoje.isBefore(r.getDataInicio()) && !hoje.isAfter(r.getDataFim())) {
                    atual = r;
                    break;
                }
            }

            if (atual == null) {
                atual = new Ranking();
                atual.setTipo(tipo);
                atual.setDataInicio(hoje.withDayOfMonth(1));
                atual.setDataFim(hoje.withDayOfMonth(hoje.lengthOfMonth()));
                atual.setStatus(StatusRankingEnum.ABERTO);
                rankingDAO.cadastrar(atual);
                if (atual.getIdRanking() <= 0) {
                    throw new IllegalStateException("Não foi possível criar o ranking do mês.");
                }
            }

            List<PosicaoRanking> posicoes = (tipo == TipoRankingEnum.INDIVIDUAL)
                    ? atual.calcularRankingUsuarios()
                    : atual.calcularRankingGrupos();

            posicaoDAO.excluirPorRanking(atual.getIdRanking());
            for (PosicaoRanking posicao : posicoes) {
                posicaoDAO.cadastrar(posicao);
            }
        }

        new RankingAllTimeService().reconstruir();
    }

    /** Resumo do fechamento: quantos prêmios foram concedidos, já existiam ou falharam. */
    public record ResultadoFechamento(int concedidas, int jaConcedidas, int naoConcedidas) {
    }

    /**
     * Fecha manualmente um ranking individual já encerrado e concede as recompensas
     * das posições elegíveis (quem concluiu ao menos uma missão no período).
     * <p>
     * As posições são recalculadas para o resultado final e cada prêmio é concedido por
     * {@link PosicaoRanking#concederRecompensa(br.com.fiap.greenlify.models.UsuarioComum)},
     * que grava prêmio e resgate na mesma transação e nunca repete o prêmio do mesmo
     * usuário no mesmo ranking. Por isso o fechamento pode ser repetido com segurança
     * (ex.: se algum prêmio falhou, como 1º lugar sem conta de luz pendente).
     *
     * @param idRanking identificador do ranking a fechar
     * @return o resumo dos prêmios
     * @throws IllegalArgumentException ranking inexistente, que não é individual ou que ainda não terminou
     */
    public ResultadoFechamento fecharRanking(int idRanking) {
        Ranking ranking = rankingDAO.buscarPorId(idRanking);
        if (ranking == null) {
            throw new IllegalArgumentException("Ranking não encontrado");
        }
        if (ranking.getTipo() != TipoRankingEnum.INDIVIDUAL) {
            throw new IllegalArgumentException("Só o ranking individual concede recompensas.");
        }
        if (ranking.getDataFim() == null || !LocalDate.now().isAfter(ranking.getDataFim())) {
            throw new IllegalArgumentException("O ranking ainda não terminou.");
        }

        List<PosicaoRanking> posicoes = ranking.calcularRankingUsuarios();
        posicaoDAO.excluirPorRanking(idRanking);
        for (PosicaoRanking posicao : posicoes) {
            posicaoDAO.cadastrar(posicao);
        }

        int concedidas = 0;
        int jaConcedidas = 0;
        int naoConcedidas = 0;
        for (PosicaoRanking posicao : posicoes) {
            if (posicao.getMissoesRealizadas() <= 0) {
                continue;
            }
            switch (posicao.concederRecompensa(posicao.getUsuario())) {
                case CONCEDIDA -> concedidas++;
                case JA_CONCEDIDA -> jaConcedidas++;
                default -> naoConcedidas++;
            }
        }

        if (ranking.getStatus() != StatusRankingEnum.FECHADO) {
            ranking.setStatus(StatusRankingEnum.FECHADO);
            rankingDAO.alterar(ranking);
        }
        return new ResultadoFechamento(concedidas, jaConcedidas, naoConcedidas);
    }

    /**
     * Confere se as credenciais são de um administrador (as mesmas do login).
     *
     * @throws IllegalArgumentException credenciais ausentes ou inválidas (401)
     * @throws SecurityException        usuário que não é administrador (403)
     */
    public void exigirAdministrador(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("Credenciais de administrador não informadas.");
        }
        if (new AdminDAO().buscarPorEmailESenha(email, senha) != null) {
            return;
        }
        if (new UsuarioComumDAO().buscarPorEmailESenha(email, senha) != null) {
            throw new SecurityException("Acesso restrito ao administrador.");
        }
        throw new IllegalArgumentException("Credenciais inválidas.");
    }

    public RankingResponseDTO buscarPorId(int id) {
        Ranking ranking = rankingDAO.buscarPorId(id);
        if (ranking == null) {
            throw new IllegalArgumentException("Ranking não encontrado");
        }
        return RankingMapper.toDto(ranking);
    }

    public List<RankingResponseDTO> listar() {
        return rankingDAO.listar()
                .stream()
                .map(RankingMapper::toDto)
                .toList();
    }

    public void cadastrar(RankingRequestDTO dto) {
        validar(dto);
        rankingDAO.cadastrar(RankingMapper.dtoToEntity(dto));
    }

    public void excluir(int id) {
        Ranking ranking = rankingDAO.buscarPorId(id);
        if (ranking == null) {
            throw new IllegalArgumentException("Ranking não encontrado");
        }
        rankingDAO.excluir(id);
    }

    public void alterar(RankingRequestDTO dto, int id) {
        Ranking existente = rankingDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Ranking não encontrado");
        }
        validar(dto);

        Ranking ranking = RankingMapper.dtoToEntity(dto);
        ranking.setIdRanking(id);
        rankingDAO.alterar(ranking);
    }

    private void validar(RankingRequestDTO dto) {
        if (dto.dataInicio() == null) {
            System.out.println("ERRO: A data de início é obrigatória.");
            throw new IllegalArgumentException("A data de início é obrigatória.");
        }
        if (dto.dataFim() == null) {
            System.out.println("ERRO: A data de fim é obrigatória.");
            throw new IllegalArgumentException("A data de fim é obrigatória.");
        }
        if (dto.dataFim().isBefore(dto.dataInicio())) {
            System.out.println("ERRO: A data de fim não pode ser anterior à data de início.");
            throw new IllegalArgumentException("A data de fim não pode ser anterior à data de início.");
        }
        if (dto.tipo() == null) {
            System.out.println("ERRO: O tipo é obrigatório.");
            throw new IllegalArgumentException("O tipo é obrigatório.");
        }
        if (dto.status() == null) {
            System.out.println("ERRO: O status é obrigatório.");
            throw new IllegalArgumentException("O status é obrigatório.");
        }
    }
}