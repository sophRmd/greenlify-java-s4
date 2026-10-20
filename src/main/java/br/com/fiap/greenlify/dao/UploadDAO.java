package br.com.fiap.greenlify.dao;

import br.com.fiap.greenlify.enums.CategoriaEnum;
import br.com.fiap.greenlify.enums.StatusContaEnum;
import br.com.fiap.greenlify.enums.StatusUploadEnum;
import br.com.fiap.greenlify.enums.TipoMetricaEnum;
import br.com.fiap.greenlify.enums.TipoMissaoEnum;
import br.com.fiap.greenlify.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO responsável pelos uploads de comprovantes enviados pelos usuários
 * para validação de uma missão contínua.
 */
public class UploadDAO {

    /**
     * Cadastra um novo upload e preenche o id gerado pelo banco no próprio objeto.
     *
     * @param upload dados do upload a ser gravado
     */
    public void cadastrar(Upload upload) {
        Connection conexao = ConnectionFactory.obterConexao();
        PreparedStatement psUpload = null;

        try {
            String sql = "INSERT INTO Upload (ds_status, dt_envio, dt_analise, ds_arquivo, ds_analise, nr_confianca_duplicidade, id_usuario, id_missao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            psUpload = conexao.prepareStatement(sql, new String[]{"ID_UPLOAD"});
            psUpload.setString(1, upload.getStatus().toString());
            psUpload.setDate(2, Date.valueOf(upload.getDataEnvio()));
            psUpload.setDate(3, upload.getDataAnalise() != null ? Date.valueOf(upload.getDataAnalise()) : null);
            psUpload.setString(4, upload.getArquivo());
            psUpload.setString(5, upload.getAnalise());
            psUpload.setInt(6, upload.getConfiancaDuplicidade());
            psUpload.setInt(7, upload.getUsuario().getIdUsuario());
            psUpload.setInt(8, upload.getMissao().getIdMissao());

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
     * Upload + Usuário (u_) + Missão contínua (m_, mc_, p_) em uma única consulta.
     * A missão só vem preenchida se for uma missão contínua (mesmo critério do
     * MissaoContinuaDAO.buscarPorId, que usa INNER JOIN com Missao_Continua).
     */
    private static final String SELECT_COMPLETO =
            "SELECT up.id_upload, up.ds_status, up.dt_envio, up.dt_analise, up.ds_arquivo, "
                    + "       up.ds_analise, up.nr_confianca_duplicidade, "
                    + "       u.id_usuario AS u_id, u.nm_usuario AS u_nome, u.ds_cpf AS u_cpf, "
                    + "       u.dt_nascimento AS u_nascimento, u.ds_email AS u_email, u.ds_senha AS u_senha, "
                    + "       u.nr_nivel_acesso AS u_nivel, u.ds_status_conta AS u_status, u.id_grupo AS u_grupo, "
                    + "       mc.id_missao AS mc_id, m.nm_missao AS m_nome, m.ds_missao AS m_descricao, "
                    + "       m.ds_requisitos AS m_requisitos, m.ds_comprovacao AS m_comprovacao, "
                    + "       m.ds_categoria AS m_categoria, m.ds_tipo_missao AS m_tipo, "
                    + "       mc.nr_limite_realizacoes AS mc_limite, mc.ds_tipo_metrica AS mc_metrica, "
                    + "       mc.vl_metrica AS mc_valor, p.id_pontuacao AS p_id, p.nr_pontuacao AS p_valor "
                    + "FROM Upload up "
                    + "LEFT JOIN Usuario u ON u.id_usuario = up.id_usuario AND u.nr_nivel_acesso = 0 "
                    + "LEFT JOIN Missao_Continua mc ON mc.id_missao = up.id_missao "
                    + "LEFT JOIN Missao m ON m.id_missao = mc.id_missao "
                    + "LEFT JOIN Pontuacao p ON p.id_pontuacao = mc.id_pontuacao ";

    /**
     * Busca um upload pelo id, carregando também o usuário e a missão
     * relacionados, quando existirem, em uma única consulta.
     *
     * @param idUpload id do upload procurado
     * @return o upload encontrado, ou {@code null} se não existir
     */
    public Upload buscarPorId(int idUpload) {
        Upload upload = null;

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement(SELECT_COMPLETO + "WHERE up.id_upload = ?")) {

            ps.setInt(1, idUpload);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    upload = mapear(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return upload;
    }

    /**
     * Lista todos os uploads cadastrados, cada um já com o usuário e a
     * missão correspondentes carregados (quando existirem), em uma única consulta.
     *
     * @return lista de uploads (pode vir vazia, nunca nula)
     */
    public List<Upload> listar() {
        List<Upload> uploads = new ArrayList<>();

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement(SELECT_COMPLETO + "ORDER BY up.id_upload");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                uploads.add(mapear(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return uploads;
    }

    private Upload mapear(ResultSet rs) throws SQLException {
        Upload upload = new Upload();
        upload.setIdUpload(rs.getInt("id_upload"));
        upload.setStatus(StatusUploadEnum.valueOf(rs.getString("ds_status")));
        upload.setDataEnvio(rs.getDate("dt_envio").toLocalDate());

        if (rs.getDate("dt_analise") != null) {
            upload.setDataAnalise(rs.getDate("dt_analise").toLocalDate());
        }

        upload.setArquivo(rs.getString("ds_arquivo"));
        upload.setAnalise(rs.getString("ds_analise"));
        upload.setConfiancaDuplicidade(rs.getInt("nr_confianca_duplicidade"));

        upload.setUsuario(mapearUsuario(rs));
        upload.setMissao(mapearMissao(rs));
        return upload;
    }

    /** Monta o usuário comum a partir das colunas u_*; devolve null se não existir. */
    private UsuarioComum mapearUsuario(ResultSet rs) throws SQLException {
        int idUsuario = rs.getInt("u_id");
        if (rs.wasNull()) {
            return null;
        }
        UsuarioComum usuario = new UsuarioComum();
        usuario.setIdUsuario(idUsuario);
        usuario.setNome(rs.getString("u_nome"));
        usuario.setCpf(rs.getString("u_cpf"));
        usuario.setDataNascimento(rs.getDate("u_nascimento").toLocalDate());
        usuario.setEmail(rs.getString("u_email"));
        usuario.setSenha(rs.getString("u_senha"));
        usuario.setNivelAcesso(rs.getInt("u_nivel"));
        usuario.setStatusConta(StatusContaEnum.valueOf(rs.getString("u_status")));

        // Igual ao UsuarioComumDAO.buscarPorId: só guarda o id do grupo do usuário.
        int idGrupo = rs.getInt("u_grupo");
        if (idGrupo > 0) {
            Grupo grupo = new Grupo();
            grupo.setIdGrupo(idGrupo);
            usuario.setGrupo(grupo);
        }
        return usuario;
    }

    /** Monta a missão contínua a partir das colunas m_*, mc_* e p_*; null se não existir. */
    private MissaoContinua mapearMissao(ResultSet rs) throws SQLException {
        int idMissao = rs.getInt("mc_id");
        if (rs.wasNull()) {
            return null;
        }
        MissaoContinua continua = new MissaoContinua();
        continua.setIdMissao(idMissao);
        continua.setNome(rs.getString("m_nome"));
        continua.setDescricao(rs.getString("m_descricao"));
        continua.setRequisitos(rs.getString("m_requisitos"));
        continua.setComoComprovar(rs.getString("m_comprovacao"));
        continua.setCategoria(CategoriaEnum.valueOf(rs.getString("m_categoria")));
        continua.setTipoMissao(TipoMissaoEnum.valueOf(rs.getString("m_tipo")));
        continua.setLimiteRealizacoes(rs.getInt("mc_limite"));
        continua.setTipoMetrica(TipoMetricaEnum.valueOf(rs.getString("mc_metrica")));
        continua.setValorMetrica(rs.getInt("mc_valor"));

        int idPontuacao = rs.getInt("p_id");
        if (!rs.wasNull()) {
            Pontuacao pontuacao = new Pontuacao();
            pontuacao.setIdPontuacao(idPontuacao);
            pontuacao.setValorBase(rs.getInt("p_valor"));
            continua.setPontuacao(pontuacao);
        }
        return continua;
    }

    /**
     * Conta quantas missões cada usuário realizou, isto é, quantos uploads
     * APROVADOS ele tem, agrupados por usuário, em uma única consulta.
     *
     * <p>Os limites são opcionais: {@code inicio} e {@code fim} (inclusive) filtram pela
     * data de envio do upload; com ambos nulos, conta tudo (ranking all time).</p>
     *
     * @param inicio primeiro dia do período, ou {@code null} para não limitar
     * @param fim    último dia do período (inclusive), ou {@code null} para não limitar
     * @return mapa idUsuario -> total de missões realizadas (usuários sem nenhuma não aparecem)
     */
    public Map<Integer, Integer> contarAprovadosPorUsuario(LocalDate inicio, LocalDate fim) {
        StringBuilder sql = new StringBuilder(
                "SELECT id_usuario, COUNT(*) AS total FROM Upload "
                        + "WHERE ds_status = ? AND id_usuario IS NOT NULL");
        if (inicio != null) {
            sql.append(" AND dt_envio >= ?");
        }
        if (fim != null) {
            sql.append(" AND dt_envio < ?");   // dia seguinte, exclusivo: pega o dia final inteiro
        }
        sql.append(" GROUP BY id_usuario");

        Map<Integer, Integer> totais = new HashMap<>();

        try (Connection con = ConnectionFactory.obterConexao();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int i = 1;
            ps.setString(i++, StatusUploadEnum.APROVADO.name());
            if (inicio != null) {
                ps.setDate(i++, Date.valueOf(inicio));
            }
            if (fim != null) {
                ps.setDate(i++, Date.valueOf(fim.plusDays(1)));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    totais.put(rs.getInt("id_usuario"), rs.getInt("total"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totais;
    }
}