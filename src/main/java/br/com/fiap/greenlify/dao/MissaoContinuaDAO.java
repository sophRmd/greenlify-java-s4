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
            psContinua.setInt(4, continua.getIdPontuacao());
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
        MissaoContinua continua = new MissaoContinua();

        PreparedStatement psMissao = null;
        PreparedStatement psContinua = null;

        try {
            psMissao = conexao.prepareStatement("SELECT * FROM missao WHERE id_missao = ?");
            psMissao.setInt(1, idMissao);
            ResultSet rsMissao = psMissao.executeQuery();
            if (rsMissao.next()) {
                continua.setIdMissao(rsMissao.getInt(1));
                continua.setNome(rsMissao.getString(2));
                continua.setDescricao(rsMissao.getString(3));
                continua.setRequisitos(rsMissao.getString(4));
                continua.setComoComprovar(rsMissao.getString(5));
                continua.setCategoria(CategoriaEnum.valueOf(rsMissao.getString(6)));
                continua.setTipoMissao(TipoMissaoEnum.valueOf(rsMissao.getString(7)));
            }
            rsMissao.close();
            psMissao.close();

            psContinua = conexao.prepareStatement("SELECT * FROM missao_continua WHERE id_missao = ?");
            psContinua.setInt(1, idMissao);
            ResultSet rsContinua = psContinua.executeQuery();
            if (rsContinua.next()) {
                continua.setLimiteRealizacoes(rsContinua.getInt(2));
                continua.setTipoMetrica(TipoMetricaEnum.valueOf(rsContinua.getString(3)));
                continua.setValorMetrica(rsContinua.getInt(4));
                continua.setIdPontuacao(rsContinua.getInt(5));
            }
            rsContinua.close();
            psContinua.close();

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
        PreparedStatement psMissao = null;

        try {
            psMissao = conexao.prepareStatement("SELECT * FROM missao WHERE ds_tipo_missao = 'MISSAO_CONTINUA'");
            ResultSet rsMissao = psMissao.executeQuery();

            while (rsMissao.next()) {
                int idMissao = rsMissao.getInt(1);
                MissaoContinua continua = buscarPorId(idMissao);
                continuas.add(continua);
            }

            rsMissao.close();
            psMissao.close();

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