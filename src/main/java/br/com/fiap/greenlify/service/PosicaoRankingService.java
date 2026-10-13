package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.PosicaoRankingDAO;
import br.com.fiap.greenlify.dto.PosicaoRankingGrupoRequestDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingGrupoResponseDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingIndividualRequestDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingIndividualResponseDTO;
import br.com.fiap.greenlify.mapper.PosicaoRankingGrupoMapper;
import br.com.fiap.greenlify.mapper.PosicaoRankingIndividualMapper;
import br.com.fiap.greenlify.models.PosicaoRanking;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PosicaoRankingService {
    private final PosicaoRankingDAO posicaoDAO;

    public PosicaoRankingService() {
        this.posicaoDAO = new PosicaoRankingDAO();
    }

    // ---------- Grupo ----------

    public PosicaoRankingGrupoResponseDTO buscarGrupoPorId(int id) {
        PosicaoRanking posicao = posicaoDAO.buscarPorId(id);
        if (posicao == null || posicao.getGrupo() == null) {
            throw new IllegalArgumentException("Posição de ranking de grupo não encontrada");
        }
        return PosicaoRankingGrupoMapper.toDto(posicao);
    }

    /**
     * Lista o ranking de grupos atual (o mais recente), ordenado pela posição.
     */
    public List<PosicaoRankingGrupoResponseDTO> listarGrupos() {
        int idRankingAtual = posicaoDAO.obterUltimoIdRankingGrupo();
        return posicaoDAO.listar()
                .stream()
                .filter(p -> p.getGrupo() != null
                        && p.getRanking() != null
                        && p.getRanking().getIdRanking() == idRankingAtual)
                .sorted(Comparator.comparingInt(PosicaoRanking::getPosicao))
                .map(PosicaoRankingGrupoMapper::toDto)
                .toList();
    }

    public void cadastrarGrupo(PosicaoRankingGrupoRequestDTO dto) {
        validarComum(dto.posicao(), dto.pontuacao(), dto.ranking() != null);
        if (dto.grupo() == null) {
            System.out.println("ERRO: O grupo é obrigatório.");
            throw new IllegalArgumentException("O grupo é obrigatório.");
        }
        posicaoDAO.cadastrar(PosicaoRankingGrupoMapper.dtoToEntity(dto));
    }

    public void alterarGrupo(PosicaoRankingGrupoRequestDTO dto, int id) {
        buscarGrupoPorId(id);
        validarComum(dto.posicao(), dto.pontuacao(), dto.ranking() != null);
        if (dto.grupo() == null) {
            System.out.println("ERRO: O grupo é obrigatório.");
            throw new IllegalArgumentException("O grupo é obrigatório.");
        }
        PosicaoRanking posicao = PosicaoRankingGrupoMapper.dtoToEntity(dto);
        posicao.setIdPosicao(id);
        posicaoDAO.alterar(posicao);
    }

    // ---------- Individual ----------

    public PosicaoRankingIndividualResponseDTO buscarIndividualPorId(int id) {
        PosicaoRanking posicao = posicaoDAO.buscarPorId(id);
        if (posicao == null || posicao.getUsuario() == null) {
            throw new IllegalArgumentException("Posição de ranking individual não encontrada");
        }
        return PosicaoRankingIndividualMapper.toDto(posicao);
    }

    /**
     * Lista o ranking individual atual (o mais recente), ordenado pela posição.
     */
    public List<PosicaoRankingIndividualResponseDTO> listarIndividuais() {
        int idRankingAtual = posicaoDAO.obterUltimoIdRankingUsuario();
        return posicaoDAO.listar()
                .stream()
                .filter(p -> p.getUsuario() != null
                        && p.getRanking() != null
                        && p.getRanking().getIdRanking() == idRankingAtual)
                .sorted(Comparator.comparingInt(PosicaoRanking::getPosicao))
                .map(PosicaoRankingIndividualMapper::toDto)
                .toList();
    }

    public void cadastrarIndividual(PosicaoRankingIndividualRequestDTO dto) {
        validarComum(dto.posicao(), dto.pontuacao(), dto.ranking() != null);
        if (dto.usuario() == null) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        posicaoDAO.cadastrar(PosicaoRankingIndividualMapper.dtoToEntity(dto));
    }

    public void alterarIndividual(PosicaoRankingIndividualRequestDTO dto, int id) {
        buscarIndividualPorId(id);
        validarComum(dto.posicao(), dto.pontuacao(), dto.ranking() != null);
        if (dto.usuario() == null) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        PosicaoRanking posicao = PosicaoRankingIndividualMapper.dtoToEntity(dto);
        posicao.setIdPosicao(id);
        posicaoDAO.alterar(posicao);
    }

    public void excluir(int id) {
        PosicaoRanking posicao = posicaoDAO.buscarPorId(id);
        if (posicao == null) {
            throw new IllegalArgumentException("Posição de ranking não encontrada");
        }
        posicaoDAO.excluir(id);
    }

    private void validarComum(int posicao, int pontuacao, boolean temRanking) {
        if (posicao <= 0) {
            System.out.println("ERRO: A posição deve ser maior que zero.");
            throw new IllegalArgumentException("A posição deve ser maior que zero.");
        }
        if (pontuacao < 0) {
            System.out.println("ERRO: A pontuação não pode ser negativa.");
            throw new IllegalArgumentException("A pontuação não pode ser negativa.");
        }
        if (!temRanking) {
            System.out.println("ERRO: O ranking é obrigatório.");
            throw new IllegalArgumentException("O ranking é obrigatório.");
        }
    }
}