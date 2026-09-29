package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.models.Fingerprint;
import br.com.fiap.greenlify.models.Upload;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de acesso a dados (DAO) responsável pelas operações de
 * persistência de {@link Fingerprint} na tabela {@code Fingerprint}.
 */
public class FingerprintDAO {
    private Connection conexao;

    /**
     * Cadastra uma nova fingerprint, vinculada ao upload informado.
     *
     * @param fingerprint fingerprint a ser cadastrada
     */
    public void cadastrar(Fingerprint fingerprint) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement(
                    "INSERT INTO Fingerprint (ds_hash_video, dt_geracao, id_upload) VALUES (?, ?, ?)"
            );
            ps.setString(1, fingerprint.getHashVideo());
            ps.setDate(2, Date.valueOf(fingerprint.getDataGeracao()));
            ps.setInt(3, fingerprint.getUpload().getIdUpload());

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
     * Atualiza os dados de uma fingerprint já cadastrada.
     *
     * @param fingerprint fingerprint com os dados atualizados
     */
    public void alterar(Fingerprint fingerprint) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("UPDATE Fingerprint SET ds_hash_video = ?, dt_geracao = ?, id_upload = ? WHERE id_fingerprint = ?");
            ps.setString(1, fingerprint.getHashVideo());
            ps.setDate(2, Date.valueOf(fingerprint.getDataGeracao()));
            ps.setInt(3, fingerprint.getUpload().getIdUpload());
            ps.setInt(4, fingerprint.getIdFingerprint());

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
     * Remove uma fingerprint do banco de dados.
     *
     * @param idFingerprint identificador da fingerprint a ser removida
     */
    public void excluir(Integer idFingerprint) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("DELETE FROM Fingerprint WHERE id_fingerprint = ?");
            ps.setInt(1, idFingerprint);
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
     * Busca uma fingerprint pelo seu identificador.
     *
     * @param idFingerprint identificador da fingerprint
     * @return a fingerprint encontrada, ou {@code null} caso não exista
     */
    public Fingerprint buscarPorId(Integer idFingerprint) {
        conexao = ConnectionFactory.obterConexao();
        Fingerprint fingerprint = null;
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Fingerprint WHERE id_fingerprint = ?");
            ps.setInt(1, idFingerprint);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                fingerprint = new Fingerprint();
                fingerprint.setIdFingerprint(rs.getInt(1));
                fingerprint.setHashVideo(rs.getString(2));
                fingerprint.setDataGeracao(rs.getDate(3).toLocalDate());

                int idUpload = rs.getInt(4);
                if (!rs.wasNull()) {
                    Upload upload = new Upload();
                    upload.setIdUpload(idUpload);
                    fingerprint.setUpload(upload);
                }
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
        return fingerprint;
    }

    /**
     * Lista todas as fingerprints cadastradas no banco de dados.
     *
     * @return lista de fingerprints cadastradas
     */
    public List<Fingerprint> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<Fingerprint> lista = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Fingerprint");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Fingerprint fingerprint = new Fingerprint();
                fingerprint.setIdFingerprint(rs.getInt(1));
                fingerprint.setHashVideo(rs.getString(2));
                fingerprint.setDataGeracao(rs.getDate(3).toLocalDate());

                int idUpload = rs.getInt(4);
                if (!rs.wasNull()) {
                    Upload upload = new Upload();
                    upload.setIdUpload(idUpload);
                    fingerprint.setUpload(upload);
                }

                lista.add(fingerprint);
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
        return lista;
    }
}