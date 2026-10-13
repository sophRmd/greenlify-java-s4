package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.RankingDAO;
import br.com.fiap.greenlify.dto.RankingRequestDTO;
import br.com.fiap.greenlify.dto.RankingResponseDTO;
import br.com.fiap.greenlify.mapper.RankingMapper;
import br.com.fiap.greenlify.models.Ranking;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RankingService {
    private final RankingDAO rankingDAO;

    public RankingService() {
        this.rankingDAO = new RankingDAO();
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