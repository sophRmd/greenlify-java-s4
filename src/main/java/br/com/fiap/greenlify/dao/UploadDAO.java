package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.enums.StatusUploadEnum;
import br.com.fiap.greenlify.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO responsável pelos uploads de comprovantes enviados pelos usuários
 * para validação de uma missão contínua.
 */
public class UploadDAO {
    private Connection conexao;

    /**
     * Cadastra um novo upload e preenche o id gerado pelo banco no próprio objeto.
     *
     * @param upload dados do upload a ser gravado
     */
    public void cadastrar(Upload upload) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement psUpload = null;

        try {
            String sql = "INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, id_usuario, id_missao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            psUpload = conexao.prepareStatement(sql, new String[]{"ID_UPLOAD"});
            psUpload.setString(1, upload.getStatus().toString());
            psUpload.setDate(2, Date.valueOf(upload.getDataEnvio()));
            psUpload.setDate(3, Date.valueOf(upload.getDataAnalise()));
            psUpload.setString(4, upload.getArquivo());
            psUpload.setString(5, upload.getAnalise());
            psUpload.setInt(6, upload.getUsuario().getIdUsuario());
            psUpload.setInt(7, upload.getMissao().getIdMissao());

            psUpload.executeUpdate();

            ResultSet rsUpload = psUpload.getGeneratedKeys();
            if (rsUpload.next()) {
                upload.setIdUpload(rsUpload.getInt(1));
            }
            rsUpload.close();

            psUpload.close();

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
     * Busca um upload pelo id, carregando também o usuário e a missão
     * relacionados, quando existirem.
     *
     * @param idUpload id do upload procurado
     * @return o upload encontrado, ou {@code null} se não existir
     */
    public Upload buscarPorId(int idUpload) {
        conexao = ConnectionFactory.obterConexao();
        PreparedStatement ps = null;
        Upload upload = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Upload WHERE id_upload = ?");
            ps.setInt(1, idUpload);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                upload = new Upload();
                upload.setIdUpload(rs.getInt(1));
                upload.setStatus(StatusUploadEnum.valueOf(rs.getString(2)));
                upload.setDataEnvio(rs.getDate(3).toLocalDate());

                if (rs.getDate(4) != null) {
                    upload.setDataAnalise(rs.getDate(4).toLocalDate());
                }

                upload.setArquivo(rs.getString(5));

                if (rs.getString(5) != null) {
                    upload.setAnalise(rs.getString(6));
                }
                int idUsuario = rs.getInt(7);
                if (idUsuario > 0) {
                    UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                    UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                    upload.setUsuario(usuario);
                }

                int idMissao = rs.getInt(8);
                if (idMissao > 0) {
                    MissaoContinuaDAO missaoDao = new MissaoContinuaDAO();
                    MissaoContinua missao = missaoDao.buscarPorId(idMissao);
                    upload.setMissao(missao);
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
        return upload;
    }

    /**
     * Lista todos os uploads cadastrados, cada um já com o usuário e a
     * missão correspondentes carregados (quando existirem).
     *
     * @return lista de uploads (pode vir vazia, nunca nula)
     */
    public List<Upload> listar() {
        conexao = ConnectionFactory.obterConexao();
        List<Upload> uploads = new ArrayList<>();
        PreparedStatement ps = null;

        try {
            ps = conexao.prepareStatement("SELECT * FROM Upload");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Upload upload = new Upload();
                upload.setIdUpload(rs.getInt(1));
                upload.setStatus(StatusUploadEnum.valueOf(rs.getString(2)));
                upload.setDataEnvio(rs.getDate(3).toLocalDate());

                if (rs.getDate(4) != null) {
                    upload.setDataAnalise(rs.getDate(4).toLocalDate());
                }

                upload.setArquivo(rs.getString(5));
                upload.setAnalise(rs.getString(6));

                int idUsuario = rs.getInt(7);
                if (idUsuario > 0) {
                    UsuarioComumDAO usuarioDao = new UsuarioComumDAO();
                    UsuarioComum usuario = usuarioDao.buscarPorId(idUsuario);
                    upload.setUsuario(usuario);
                }

                int idMissao = rs.getInt(8);
                if (idMissao > 0) {
                    MissaoContinuaDAO missaoDao = new MissaoContinuaDAO();
                    MissaoContinua missao = missaoDao.buscarPorId(idMissao);
                    upload.setMissao(missao);
                }

                uploads.add(upload);
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
        return uploads;
    }
}