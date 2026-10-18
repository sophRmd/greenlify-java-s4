package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.PosicaoRankingDAO;
import br.com.fiap.greenlify.dao.RankingDAO;
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

    public RankingService() {
        this.rankingDAO = new RankingDAO();
        this.posicaoDAO = new PosicaoRankingDAO();
    }

    /** O ranking é atualizado diariamente, logo após a meia-noite. */
    @Scheduled(cron = "0 5 0 * * *")
    public void atualizarRankingsDiario() {
        atualizarRankings();
    }

    /**
     * Recalcula os rankings (individual e de grupos) do mês atual: fecha os
     * períodos que já terminaram, cria o ranking do mês se ainda não existir e
     * regrava as posições usando as regras do model {@link Ranking}
     * (individual: pontos da carteira; grupos: total de selos dos integrantes).
     *
     * @throws IllegalStateException se o banco não confirmar a criação do ranking
     */
    public void atualizarRankings() {
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
