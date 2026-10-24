package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.PosicaoRankingDAO;
import br.com.fiap.greenlify.dao.UsuarioSeloDAO;
import br.com.fiap.greenlify.dto.PosicaoRankingGrupoResponseDTO;
import br.com.fiap.greenlify.dto.PosicaoRankingIndividualResponseDTO;
import br.com.fiap.greenlify.mapper.PosicaoRankingGrupoMapper;
import br.com.fiap.greenlify.mapper.PosicaoRankingIndividualMapper;
import br.com.fiap.greenlify.models.PosicaoRanking;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class PosicaoRankingService {
    private final PosicaoRankingDAO posicaoDAO;
    private final UsuarioSeloDAO usuarioSeloDAO;

    public PosicaoRankingService() {
        this.posicaoDAO = new PosicaoRankingDAO();
        this.usuarioSeloDAO = new UsuarioSeloDAO();
    }

    // ---------- Grupo ----------

    public PosicaoRankingGrupoResponseDTO buscarGrupoPorId(int id) {
        PosicaoRanking posicao = posicaoDAO.buscarPorId(id);
        if (posicao == null || posicao.getGrupo() == null) {
            throw new IllegalArgumentException("Posição de ranking de grupo não encontrada");
        }
        // Pontuação e posição calculadas na hora, como na lista (não o valor gravado).
        if (posicao.getRanking() != null) {
            for (PosicaoRanking calculada : calcularGrupos(posicao.getRanking().getIdRanking())) {
                if (calculada.getIdPosicao() == posicao.getIdPosicao()) {
                    return PosicaoRankingGrupoMapper.toDto(calculada);
                }
            }
        }
        return PosicaoRankingGrupoMapper.toDto(posicao);
    }

    /**
     * Lista o ranking de grupos atual (o mais recente), ordenado pela posição. A pontuação de cada
     * grupo é a quantidade de selos coletados pelos integrantes, calculada na hora (a mesma conta
     * do resumo do grupo), e não o valor gravado em Posicao_Ranking, que só muda quando o ranking
     * é atualizado.
     */
    public List<PosicaoRankingGrupoResponseDTO> listarGrupos() {
        int idRankingAtual = posicaoDAO.obterUltimoIdRankingGrupo();
        return calcularGrupos(idRankingAtual)
                .stream()
                .map(PosicaoRankingGrupoMapper::toDto)
                .toList();
    }

    /**
     * Posições de grupo de um ranking com a pontuação recalculada a partir de Usuario_Selo
     * (selos coletados pelos integrantes atuais de cada grupo), já reordenadas e renumeradas.
     * Empate: grupo cadastrado primeiro, como em {@code Ranking.calcularRankingGrupos}. Não grava nada.
     */
    private List<PosicaoRanking> calcularGrupos(int idRanking) {
        List<PosicaoRanking> posicoes = new ArrayList<>(posicaoDAO.listarPorRanking(idRanking)
                .stream()
                .filter(p -> p.getGrupo() != null
                        && p.getRanking() != null
                        && p.getRanking().getIdRanking() == idRanking)
                .toList());

        Map<Integer, Integer> selosPorGrupo = usuarioSeloDAO.contarSelosPorGrupo();
        for (PosicaoRanking p : posicoes) {
            p.setPontuacao(selosPorGrupo.getOrDefault(p.getGrupo().getIdGrupo(), 0));
        }

        posicoes.sort(Comparator.comparingInt(PosicaoRanking::getPontuacao).reversed()
                .thenComparingInt(p -> p.getGrupo().getIdGrupo()));
        for (int i = 0; i < posicoes.size(); i++) {
            posicoes.get(i).setPosicao(i + 1);
        }
        return posicoes;
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
        return posicaoDAO.listarPorRanking(idRankingAtual)
                .stream()
                .filter(p -> p.getUsuario() != null
                        && p.getRanking() != null
                        && p.getRanking().getIdRanking() == idRankingAtual)
                .sorted(Comparator.comparingInt(PosicaoRanking::getPosicao))
                .map(PosicaoRankingIndividualMapper::toDto)
                .toList();
    }
}