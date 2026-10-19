package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.enums.DificuldadeEnum;
import br.com.fiap.greenlify.enums.ImpactoEnum;
import br.com.fiap.greenlify.models.Pontuacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link Pontuacao} na tabela {@code Pontuacao}.
 */
public class PontuacaoDAO {
    private Connection conexao;

    /**
     * Cadastra uma nova pontuação.
     *
     * @param pontuacao pontuação a ser cadastrada
     */
    public void cadastrar(Pontuacao pontuacao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Pontuacao (id_pontuacao, ds_impacto, ds_dificuldade, nr_pontuacao) " +
                    "VALUES (?, ?, ?, ?)";
            ps = conexao.prepareStatement(sql);
            ps.setInt(1, pontuacao.getIdPontuacao());
            ps.setString(2, pontuacao.getImpacto().toString());
            ps.setString(3, pontuacao.getDificuldade().toString());
            ps.setInt(4, pontuacao.getValorBase());

            ps.executeUpdate();
            ps.close();

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
    }

    /**
     * Atualiza os dados de uma pontuação já cadastrada.
     *
     * @param pontuacao pontuação com os dados atualizados
     */
    public void alterar(Pontuacao pontuacao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE Pontuacao SET ds_impacto = ?, ds_dificuldade = ?, nr_pontuacao = ? " +
                    "WHERE id_pontuacao = ?";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, pontuacao.getImpacto().toString());
            ps.setString(2, pontuacao.getDificuldade().toString());
            ps.setInt(3, pontuacao.getValorBase());
            ps.setInt(4, pontuacao.getIdPontuacao());

            ps.executeUpdate();
            ps.close();

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
    }

    /**
     * Remove uma pontuação do banco de dados.
     *
     * @param idPontuacao identificador da pontuação a ser removida
     */
    public void excluir(int idPontuacao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Pontuacao WHERE id_pontuacao = ?");
            ps.setInt(1, idPontuacao);
            ps.executeUpdate();
            ps.close();

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
    }

    /**
     * Busca uma pontuação pelo seu identificador.
     *
     * @param idPontuacao identificador da pontuação
     * @return a pontuação encontrada, ou {@code null} caso não exista
     */
    public Pontuacao buscarPorId(int idPontuacao) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        Pontuacao pontuacao = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Pontuacao WHERE id_pontuacao = ?");
            ps.setInt(1, idPontuacao);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pontuacao = new Pontuacao();
                pontuacao.setIdPontuacao(rs.getInt(1));
                pontuacao.setImpacto(ImpactoEnum.valueOf(rs.getString(2)));
                pontuacao.setDificuldade(DificuldadeEnum.valueOf(rs.getString(3)));
                pontuacao.setValorBase(rs.getInt(4));
            }

            rs.close();
            ps.close();

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
        return pontuacao;
    }

    /**
     * Busca a pontuação correspondente a uma combinação específica de
     * impacto e dificuldade.
     *
     * @param impacto     nível de impacto ambiental da missão
     * @param dificuldade nível de dificuldade da missão
     * @return a pontuação encontrada, ou {@code null} caso não exista
     */
    public Pontuacao buscarPorImpactoEDificuldade(String impacto, String dificuldade) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        Pontuacao pontuacao = null;

        try {
            String sql = "SELECT * FROM Pontuacao WHERE ds_impacto = ? AND ds_dificuldade = ?";
            ps = conexao.prepareStatement(sql);
            ps.setString(1, impacto);
            ps.setString(2, dificuldade);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                pontuacao = new Pontuacao();
                pontuacao.setIdPontuacao(rs.getInt(1));
                pontuacao.setImpacto(ImpactoEnum.valueOf(rs.getString(2)));
                pontuacao.setDificuldade(DificuldadeEnum.valueOf(rs.getString(3)));
                pontuacao.setValorBase(rs.getInt(4));
            }

            rs.close();
            ps.close();

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
        return pontuacao;
    }

    /**
     * Lista todas as pontuações cadastradas no banco de dados, em uma única
     * consulta (uma única conexão).
     *
     * @return lista de pontuações cadastradas
     */
    public List<Pontuacao> listar() {
        List<Pontuacao> pontuacoes = new ArrayList<>();

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM Pontuacao");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Pontuacao pontuacao = new Pontuacao();
                pontuacao.setIdPontuacao(rs.getInt(1));
                pontuacao.setImpacto(ImpactoEnum.valueOf(rs.getString(2)));
                pontuacao.setDificuldade(DificuldadeEnum.valueOf(rs.getString(3)));
                pontuacao.setValorBase(rs.getInt(4));
                pontuacoes.add(pontuacao);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pontuacoes;
    }
}