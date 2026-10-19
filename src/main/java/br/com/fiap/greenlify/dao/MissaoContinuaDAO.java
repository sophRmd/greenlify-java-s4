package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.enums.CategoriaEnum;
import br.com.fiap.greenlify.enums.TipoMetricaEnum;
import br.com.fiap.greenlify.enums.TipoMissaoEnum;
import br.com.fiap.greenlify.models.Missao;
import br.com.fiap.greenlify.models.MissaoContinua;
import br.com.fiap.greenlify.models.Pontuacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link MissaoContinua}, envolvendo as tabelas
 * {@code Missao} e {@code Missao_Continua}.
 */
public class MissaoContinuaDAO {
    private Connection conexao;

    /**
     * Cadastra uma nova missão contínua, inserindo os dados básicos na
     * tabela {@code Missao} e os dados específicos na tabela
     * {@code Missao_Continua}, dentro de uma mesma transação.
     *
     * @param missao    dados básicos da missão a ser cadastrada
     * @param continua  dados específicos da missão contínua
     * @param pontuacao pontuação a ser associada à missão
     */
    public void cadastrarMissao(Missao missao, MissaoContinua continua, Pontuacao pontuacao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement psMissao = null;
        PreparedStatement psContinua = null;

        try {
            conexao.setAutoCommit(false);

            psMissao = conexao.prepareStatement("INSERT INTO Missao (nm_missao, ds_missao, ds_requisitos, " +
                            "ds_comprovacao, ds_categoria, ds_tipo_missao) VALUES (?, ?, ?, ?, ?, ?)",
                    new String[]{"ID_MISSAO"});
            psMissao.setString(1, missao.getNome());
            psMissao.setString(2, missao.getDescricao());
            psMissao.setString(3, missao.getRequisitos());
            psMissao.setString(4, missao.getComoComprovar());
            psMissao.setString(5, missao.getCategoria().toString());
            psMissao.setString(6, missao.getTipoMissao().toString());
            psMissao.executeUpdate();

            ResultSet rsGerado = psMissao.getGeneratedKeys();
            if (rsGerado.next()) {
                missao.setIdMissao(rsGerado.getInt(1));
                continua.setIdMissao(rsGerado.getInt(1));
            }
            rsGerado.close();

            psContinua = conexao.prepareStatement("INSERT INTO Missao_Continua (id_missao, nr_limite_realizacoes, ds_tipo_metrica," +
                    "vl_metrica, id_pontuacao) VALUES (?, ?, ?, ?, ?)");
            psContinua.setInt(1, missao.getIdMissao());
            psContinua.setInt(2, continua.getLimiteRealizacoes());
            psContinua.setString(3, continua.getTipoMetrica().toString());
            psContinua.setInt(4, continua.getValorMetrica());
            psContinua.setInt(5, pontuacao.getIdPontuacao());
            psContinua.executeUpdate();

            conexao.commit();

            psMissao.close();
            psContinua.close();

        } catch (SQLException e) {
            try {
                if (conexao != null) {
                    conexao.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Atualiza os dados de uma missão contínua já cadastrada, alterando
     * tanto os dados básicos da missão quanto os dados específicos da
     * missão contínua, dentro de uma mesma transação.
     *
     * @param continua dados específicos da missão contínua a serem atualizados
     * @param missao   dados básicos da missão a serem atualizados
     */
    public void alterar(MissaoContinua continua, Missao missao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement psMissao = null;
        PreparedStatement psContinua = null;

        try {
            conexao.setAutoCommit(false);

            psMissao = conexao.prepareStatement("UPDATE Missao SET nm_missao = ?, ds_missao = ?, ds_requisitos = ?, ds_comprovacao = ?," +
                    " ds_categoria = ?, ds_tipo_missao = ? WHERE id_missao = ?");
            psMissao.setString(1, missao.getNome());
            psMissao.setString(2, missao.getDescricao());
            psMissao.setString(3, missao.getRequisitos());
            psMissao.setString(4, missao.getComoComprovar());
            psMissao.setString(5, missao.getCategoria().toString());
            psMissao.setString(6, missao.getTipoMissao().toString());
            psMissao.setInt(7, missao.getIdMissao());
            psMissao.executeUpdate();

            psContinua = conexao.prepareStatement("UPDATE Missao_continua SET nr_limite_realizacoes = ?, " +
                    "ds_tipo_metrica = ?, vl_metrica = ?, id_pontuacao = ? WHERE id_missao = ?");
            psContinua.setInt(1, continua.getLimiteRealizacoes());
            psContinua.setString(2, continua.getTipoMetrica().toString());
            psContinua.setInt(3, continua.getValorMetrica());
            psContinua.setInt(4, continua.getPontuacao().getIdPontuacao());
            psContinua.setInt(5, continua.getIdMissao());
            psContinua.executeUpdate();

            conexao.commit();

            psMissao.close();
            psContinua.close();

        } catch (SQLException e) {
            try {
                if (conexao != null) {
                    conexao.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove uma missão contínua, excluindo tanto o registro específico na
     * tabela {@code Missao_Continua} quanto o registro básico na tabela
     * {@code Missao}, dentro de uma mesma transação.
     *
     * @param continua missão contínua a ser removida
     */
    public void excluir(MissaoContinua continua) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement psMissao = null;
        PreparedStatement psContinua = null;

        try {
            conexao.setAutoCommit(false);

            psContinua = conexao.prepareStatement("DELETE FROM Missao_continua WHERE id_missao = ?");
            psContinua.setInt(1, continua.getIdMissao());
            psContinua.executeUpdate();

            psMissao = conexao.prepareStatement("DELETE FROM Missao WHERE id_missao = ?");
            psMissao.setInt(1, continua.getIdMissao());
            psMissao.executeUpdate();

            conexao.commit();

            psContinua.close();
            psMissao.close();

        } catch (SQLException e) {
            try {
                if (conexao != null) {
                    conexao.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Busca uma missão contínua pelo seu identificador, combinando os
     * dados das tabelas {@code Missao} e {@code Missao_Continua}.
     *
     * @param idMissao identificador da missão
     * @return a missão contínua encontrada; os campos permanecem com os
     *         valores padrão caso o identificador não seja encontrado
     */
    public MissaoContinua buscarPorId(Integer idMissao) {
        conexao = ConnectionFactory.obterConexao();
        MissaoContinua continua = null;

        String sql = "SELECT m.id_missao, m.nm_missao, m.ds_missao, " +
                "m.ds_requisitos, m.ds_comprovacao, m.ds_categoria, " +
                "m.ds_tipo_missao, mc.nr_limite_realizacoes, " +
                "mc.ds_tipo_metrica, mc.vl_metrica, " +
                "p.id_pontuacao, p.nr_pontuacao " +
                "FROM Missao m " +
                "INNER JOIN Missao_Continua mc ON mc.id_missao = m.id_missao " +
                "LEFT JOIN Pontuacao p ON p.id_pontuacao = mc.id_pontuacao " +
                "WHERE m.id_missao = ?";

        try (PreparedStatement ps = conexao.prepareStatement(sql)) {
            ps.setInt(1, idMissao);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    continua = new MissaoContinua();

                    // dados da missão
                    continua.setIdMissao(rs.getInt("id_missao"));
                    continua.setNome(rs.getString("nm_missao"));
                    continua.setDescricao(rs.getString("ds_missao"));
                    continua.setRequisitos(rs.getString("ds_requisitos"));
                    continua.setComoComprovar(rs.getString("ds_comprovacao"));
                    continua.setCategoria(
                            CategoriaEnum.valueOf(rs.getString("ds_categoria")));
                    continua.setTipoMissao(
                            TipoMissaoEnum.valueOf(rs.getString("ds_tipo_missao")));

                    // dados da missão contínua
                    continua.setLimiteRealizacoes(
                            rs.getInt("nr_limite_realizacoes"));
                    continua.setTipoMetrica(
                            TipoMetricaEnum.valueOf(rs.getString("ds_tipo_metrica")));
                    continua.setValorMetrica(rs.getInt("vl_metrica"));

                    // pontuação
                    int idPontuacao = rs.getInt("id_pontuacao");

                    if (!rs.wasNull()) {
                        Pontuacao pontuacao = new Pontuacao();
                        pontuacao.setIdPontuacao(idPontuacao);
                        pontuacao.setValorBase(rs.getInt("nr_pontuacao"));
                        continua.setPontuacao(pontuacao);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return continua;
    }
    /**
     * Lista todas as missões contínuas cadastradas no banco de dados.
     *
     * @return lista de missões contínuas cadastradas
     */
    public List<MissaoContinua> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<MissaoContinua> continuas = new ArrayList<>();

        String sql = "SELECT m.id_missao, m.nm_missao, m.ds_missao, " +
                "m.ds_requisitos, m.ds_comprovacao, m.ds_categoria, " +
                "m.ds_tipo_missao, mc.nr_limite_realizacoes, " +
                "mc.ds_tipo_metrica, mc.vl_metrica, " +
                "p.id_pontuacao, p.nr_pontuacao " +
                "FROM Missao m " +
                "INNER JOIN Missao_Continua mc ON mc.id_missao = m.id_missao " +
                "LEFT JOIN Pontuacao p ON p.id_pontuacao = mc.id_pontuacao " +
                "WHERE m.ds_tipo_missao = 'MISSAO_CONTINUA'";

        try (PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MissaoContinua continua = new MissaoContinua();

                // dados da missão
                continua.setIdMissao(rs.getInt(1));
                continua.setNome(rs.getString(2));
                continua.setDescricao(rs.getString(3));
                continua.setRequisitos(rs.getString(4));
                continua.setComoComprovar(rs.getString(5));
                continua.setCategoria(CategoriaEnum.valueOf(rs.getString(6)));
                continua.setTipoMissao(TipoMissaoEnum.valueOf(rs.getString(7)));

                continua.setLimiteRealizacoes(rs.getInt(8));
                continua.setTipoMetrica(TipoMetricaEnum.valueOf(rs.getString(9)));
                continua.setValorMetrica(rs.getInt(10));

                int idPontuacao = rs.getInt(11);

                if (!rs.wasNull()) {
                    Pontuacao pontuacao = new Pontuacao();
                    pontuacao.setIdPontuacao(idPontuacao);
                    pontuacao.setValorBase(rs.getInt(12));
                    continua.setPontuacao(pontuacao);
                }

                continuas.add(continua);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conexao != null && !conexao.isClosed()) {
                    conexao.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return continuas;
    }
}