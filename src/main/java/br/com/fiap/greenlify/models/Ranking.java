package br.com.fiap.greenlify.models;

import br.com.fiap.greenlify.dao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Representa um período de ranking dentro da plataforma GreenLify,
 * responsável por calcular e listar a classificação de usuários e de
 * grupos com base na pontuação acumulada.
 */
public class Ranking {

    private int idRanking;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    /**
     * Calcula o ranking de usuários com base na quantidade de pontos
     * disponível na carteira de cada um, ordenando do maior para o menor
     * saldo e atribuindo a posição correspondente.
     *
     * @return lista de posições de ranking dos usuários, já ordenada e
     *         numerada, ou lista vazia caso não existam usuários cadastrados
     */
    public List<PosicaoRanking> calcularRankingUsuarios() {
        UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
        CarteiraDAO carteiraDAO = new CarteiraDAO();

        List<UsuarioComum> todosUsuarios = usuarioDAO.listar();
        List<PosicaoRanking> posicoes = new ArrayList<>();

        if (todosUsuarios == null || todosUsuarios.isEmpty()) {
            return posicoes;
        }

        for (UsuarioComum usuario : todosUsuarios) {
            Carteira carteira = carteiraDAO.buscarPorIdUsuario(usuario.getIdUsuario());

            int pontos = 0;
            if (carteira != null) {
                pontos = carteira.getQtdPontos();
            }

            PosicaoRanking pr = new PosicaoRanking();
            pr.setUsuario(usuario);
            pr.setPontuacao(pontos);
            pr.setRanking(this);

            posicoes.add(pr);
        }

        posicoes.sort(new Comparator<PosicaoRanking>() {
            @Override
            public int compare(PosicaoRanking p1, PosicaoRanking p2) {
                return Integer.compare(p2.getPontuacao(), p1.getPontuacao());
            }
        });

        for (int i = 0; i < posicoes.size(); i++) {
            posicoes.get(i).setPosicao(i + 1);
        }

        return posicoes;
    }

    /**
     * Exibe no console o ranking de usuários, considerando apenas as
     * posições que possuem um usuário associado.
     *
     * @param posicoes lista de posições de ranking a ser exibida
     */
    public void listarRankingUsuarios(List<PosicaoRanking> posicoes) {
        List<PosicaoRanking> posicoesUsuarios = new ArrayList<>();

        if (posicoes != null) {
            for (PosicaoRanking pr : posicoes) {
                if (pr.getUsuario() != null) {
                    posicoesUsuarios.add(pr);
                }
            }
        }

        if (!posicoesUsuarios.isEmpty()) {
            System.out.println("=== RANKING DE USUÁRIOS ===");
            for (PosicaoRanking pr : posicoesUsuarios) {
                System.out.println("\n" + pr.getPosicao() + "° lugar");
                System.out.println("Nome: " + pr.getUsuario().getNome());
                System.out.println("Pontuação: " + pr.getPontuacao());
            }
        } else {
            System.out.println("Nenhum usuário encontrado no ranking.");
        }
    }

    /**
     * Calcula o ranking de grupos com base na quantidade de selos
     * conquistados pelos usuários de cada grupo (um selo conta como
     * uma unidade, independentemente da pontuação necessária para
     * obtê-lo), ordenando do maior para o menor total e atribuindo a
     * posição correspondente.
     *
     * @return lista de posições de ranking dos grupos, já ordenada e
     *         numerada, ou lista vazia caso não existam grupos cadastrados
     */
    public List<PosicaoRanking> calcularRankingGrupos() {
        GrupoDAO grupoDAO = new GrupoDAO();
        UsuarioComumDAO usuarioDAO = new UsuarioComumDAO();
        UsuarioSeloDAO usuarioSeloDAO = new UsuarioSeloDAO();

        List<Grupo> todosGrupos = grupoDAO.listar();
        List<UsuarioComum> todosUsuarios = usuarioDAO.listar();
        List<PosicaoRanking> posicoes = new ArrayList<>();

        if (todosGrupos == null || todosGrupos.isEmpty()) {
            return posicoes;
        }

        for (Grupo grupo : todosGrupos) {
            int totalSelosGrupo = 0;

            if (todosUsuarios != null) {
                for (UsuarioComum usuario : todosUsuarios) {
                    boolean pertenceGrupo = false;
                    if (usuario.getGrupo() != null) {
                        pertenceGrupo = (usuario.getGrupo().getIdGrupo() == grupo.getIdGrupo());
                    }

                    if (pertenceGrupo) {
                        List<UsuarioSelo> selosDoUsuario = usuarioSeloDAO.buscarPorIdUsuario(usuario.getIdUsuario());
                        if (selosDoUsuario != null) {
                            for (UsuarioSelo us : selosDoUsuario) {
                                if (us.getSelo() != null) {
                                    totalSelosGrupo += 1;
                                }
                            }
                        }
                    }
                }
            }

            PosicaoRanking pr = new PosicaoRanking();
            pr.setGrupo(grupo);
            pr.setPontuacao(totalSelosGrupo);
            pr.setRanking(this);

            posicoes.add(pr);
        }

        posicoes.sort(Comparator.comparingInt(PosicaoRanking::getPontuacao).reversed());
        for (int i = 0; i < posicoes.size(); i++) {
            posicoes.get(i).setPosicao(i + 1);
        }

        return posicoes;
    }

    /**
     * Exibe no console o ranking de grupos referente ao período de ranking
     * mais recente, considerando apenas as posições que possuem um grupo
     * associado.
     *
     * @param listaRanking lista de posições de ranking a ser filtrada e exibida
     */
    public void listarRankingGrupos(List<PosicaoRanking> listaRanking) {
        PosicaoRankingDAO posicaoDAO = new PosicaoRankingDAO();
        int ultimoIdRankingGrupo = posicaoDAO.obterUltimoIdRankingGrupo();

        List<PosicaoRanking> posicoesGrupos = new ArrayList<>();

        if (listaRanking != null) {
            for (PosicaoRanking pr : listaRanking) {
                if (pr.getGrupo() != null) {
                    if (pr.getRanking() == null || pr.getRanking().getIdRanking() == 0
                            || pr.getRanking().getIdRanking() == ultimoIdRankingGrupo) {
                        posicoesGrupos.add(pr);
                    }
                }
            }
        }

        System.out.println("\n=== RANKING DE GRUPOS  ===");

        if (posicoesGrupos.isEmpty()) {
            System.out.println("Nenhum grupo encontrado para exibir no ranking.");
            return;
        }

        for (PosicaoRanking pr : posicoesGrupos) {
            System.out.println("\n" + pr.getPosicao() + "º Lugar - Grupo: "
                    + pr.getGrupo().getNome() + " | Selos Totais: " + pr.getPontuacao());
        }
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public int getIdRanking() {
        return idRanking;
    }

    public void setIdRanking(int idRanking) {
        this.idRanking = idRanking;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }
}