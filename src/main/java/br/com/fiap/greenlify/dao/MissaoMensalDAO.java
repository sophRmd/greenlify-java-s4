package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.enums.CategoriaEnum;
import br.com.fiap.greenlify.enums.TipoMissaoEnum;
import br.com.fiap.greenlify.models.Missao;
import br.com.fiap.greenlify.models.MissaoMensal;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link MissaoMensal}, envolvendo as tabelas
 * {@code Missao} e {@code Missao_Mensal}.
 */
public class MissaoMensalDAO {
    private Connection conexao;

    /**
     * Cadastra uma nova missão mensal, inserindo os dados básicos na
     * tabela {@code Missao} e os dados específicos na tabela
     * {@code Missao_Mensal}, dentro de uma mesma transação.
     *
     * @param missao dados básicos da missão a ser cadastrada
     * @param mensal dados específicos da missão mensal
     */
    public void cadastrarMissao(Missao missao, MissaoMensal mensal) {
        if (mensal.getDataInicio() == null || mensal.getDataFim() == null) {
            System.out.println("Erro ao cadastrar missão.");
            return;
        }

        conexao = ConnectionFactory.obterConexao();
        PreparedStatement psMissao = null;
        PreparedStatement psMensal = null;

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
                mensal.setIdMissao(rsGerado.getInt(1));
            }
            rsGerado.close();

            psMensal = conexao.prepareStatement("INSERT INTO Missao_Mensal (id_missao, nr_meta_missoes, nr_progresso_missoes, dt_inicio, dt_fim) VALUES (?, ?, ?, ?, ?)");
            psMensal.setInt(1, missao.getIdMissao());
            psMensal.setInt(2, mensal.getMeta());
            psMensal.setInt(3, mensal.getProgressoAtual());
            psMensal.setDate(4, Date.valueOf(mensal.getDataInicio()));
            psMensal.setDate(5, Date.valueOf(mensal.getDataFim()));
            psMensal.executeUpdate();

            conexao.commit();

            psMissao.close();
            psMensal.close();

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
     * Atualiza os dados de uma missão mensal já cadastrada, alterando
     * tanto os dados básicos da missão quanto os dados específicos da
     * missão mensal, dentro de uma mesma transação.
     *
     * @param mensal dados específicos da missão mensal a serem atualizados
     * @param missao dados básicos da missão a serem atualizados
     */
    public void alterar(MissaoMensal mensal, Missao missao) {
        if (mensal.getDataInicio() == null || mensal.getDataFim() == null) {
            System.out.println("Erro ao alterar missão.");
            return;
        }

        conexao = ConnectionFactory.obterConexao();
        PreparedStatement psMissao = null;
        PreparedStatement psMensal = null;

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

            psMensal = conexao.prepareStatement("UPDATE Missao_Mensal SET nr_meta_missoes = ?, nr_progresso_missoes = ?, dt_inicio = ?, dt_fim = ? WHERE id_missao = ?");
            psMensal.setInt(1, mensal.getMeta());
            psMensal.setInt(2, mensal.getProgressoAtual());
            psMensal.setDate(3, Date.valueOf(mensal.getDataInicio()));
            psMensal.setDate(4, Date.valueOf(mensal.getDataFim()));
            psMensal.setInt(5, missao.getIdMissao());
            psMensal.executeUpdate();

            conexao.commit();

            psMissao.close();
            psMensal.close();

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
     * Remove uma missão mensal, excluindo tanto o registro específico na
     * tabela {@code Missao_Mensal} quanto o registro básico na tabela
     * {@code Missao}, dentro de uma mesma transação.
     *
     * @param mensal missão mensal a ser removida
     */
    public void excluir(MissaoMensal mensal) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement psMissao = null;
        PreparedStatement psMensal = null;

        try {
            conexao.setAutoCommit(false);

            psMensal = conexao.prepareStatement("DELETE FROM Missao_Mensal WHERE id_missao = ?");
            psMensal.setInt(1, mensal.getIdMissao());
            psMensal.executeUpdate();

            psMissao = conexao.prepareStatement("DELETE FROM Missao WHERE id_missao = ?");
            psMissao.setInt(1, mensal.getIdMissao());
            psMissao.executeUpdate();

            conexao.commit();

            psMensal.close();
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
     * Busca uma missão mensal pelo seu identificador, combinando os
     * dados das tabelas {@code Missao} e {@code Missao_Mensal}.
     *
     * @param idMissao identificador da missão
     * @return a missão mensal encontrada; os campos permanecem com os
     *         valores padrão caso o identificador não seja encontrado
     */
    public MissaoMensal buscarPorId(Integer idMissao) {
        conexao = ConnectionFactory.obterConexao();
        MissaoMensal mensal = new MissaoMensal();

        PreparedStatement psMissao = null;
        PreparedStatement psMensal = null;

        try {
            psMissao = conexao.prepareStatement("SELECT * FROM Missao WHERE id_missao = ?");
            psMissao.setInt(1, idMissao);
            ResultSet rsMissao = psMissao.executeQuery();
            if (rsMissao.next()) {
                mensal.setIdMissao(rsMissao.getInt(1));
                mensal.setNome(rsMissao.getString(2));
                mensal.setDescricao(rsMissao.getString(3));
                mensal.setRequisitos(rsMissao.getString(4));
                mensal.setComoComprovar(rsMissao.getString(5));
                mensal.setCategoria(CategoriaEnum.valueOf(rsMissao.getString(6)));
                mensal.setTipoMissao(TipoMissaoEnum.valueOf(rsMissao.getString(7)));
            }
            rsMissao.close();
            psMissao.close();

            psMensal = conexao.prepareStatement("SELECT * FROM Missao_Mensal WHERE id_missao = ?");
            psMensal.setInt(1, idMissao);
            ResultSet rsMensal = psMensal.executeQuery();
            if (rsMensal.next()) {
                mensal.setMeta(rsMensal.getInt(2));
                mensal.setProgressoAtual(rsMensal.getInt(3));
                if (rsMensal.getDate(4) != null) {
                    mensal.setDataInicio(rsMensal.getDate(4).toLocalDate());
                }
                if (rsMensal.getDate(5) != null) {
                    mensal.setDataFim(rsMensal.getDate(5).toLocalDate());
                }
            }
            rsMensal.close();
            psMensal.close();

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
        return mensal;
    }

    /**
     * Lista todas as missões mensais cadastradas no banco de dados.
     *
     * @return lista de missões mensais cadastradas
     */
    public List<MissaoMensal> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<MissaoMensal> mensais = new ArrayList<>();
        PreparedStatement psMissao = null;

        try {
            psMissao = conexao.prepareStatement("SELECT * FROM Missao WHERE ds_tipo_missao = 'MISSAO_MENSAL'");
            ResultSet rsMissao = psMissao.executeQuery();

            while (rsMissao.next()) {
                int idMissao = rsMissao.getInt(1);
                MissaoMensal mensal = buscarPorId(idMissao);
                mensais.add(mensal);
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
        return mensais;
    }
}