package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.GrupoDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.GrupoRequestDTO;
import br.com.fiap.greenlify.dto.GrupoResponseDTO;
import br.com.fiap.greenlify.mapper.GrupoMapper;
import br.com.fiap.greenlify.models.Grupo;
import br.com.fiap.greenlify.models.UsuarioComum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GrupoService {

    // Status atribuído a todo grupo novo, já que o DTO de cadastro não o informa.
    // Ajuste para o valor que representa "ativo" no seu banco.
    private static final int STATUS_INICIAL = 1;

    private final GrupoDAO grupoDAO;
    private final UsuarioComumDAO usuarioDAO;

    public GrupoService() {
        this.grupoDAO = new GrupoDAO();
        this.usuarioDAO = new UsuarioComumDAO();
    }

    /** Nº de integrantes por grupo, contado a partir de Usuario.id_grupo (a coluna nr_usuarios não é atualizada ao entrar/sair). */
    private Map<Integer, Integer> contarIntegrantes() {
        Map<Integer, Integer> total = new HashMap<>();
        for (UsuarioComum u : usuarioDAO.listar()) {
            if (u.getGrupo() != null) {
                total.merge(u.getGrupo().getIdGrupo(), 1, Integer::sum);
            }
        }
        return total;
    }

    public List<GrupoResponseDTO> listar() {
        Map<Integer, Integer> integrantes = contarIntegrantes();
        return grupoDAO.listar()
                .stream()
                .peek(g -> g.setQtdUsuarios(integrantes.getOrDefault(g.getIdGrupo(), 0)))
                .map(GrupoMapper::toDto)
                .toList();
    }

    public GrupoResponseDTO buscarPorId(int id) {
        var grupo = grupoDAO.buscarPorId(id);
        if (grupo == null)
            throw new IllegalArgumentException("Grupo não encontrado");
        grupo.setQtdUsuarios(contarIntegrantes().getOrDefault(grupo.getIdGrupo(), 0));
        return GrupoMapper.toDto(grupo);
    }

    public void cadastrar(GrupoRequestDTO dto) {
        validar(dto);

        Grupo grupo = GrupoMapper.dtoToEntity(dto);
        grupo.setStatusGrupo(STATUS_INICIAL);
        grupoDAO.cadastrar(grupo);
    }

    public void atualizar(int id, GrupoRequestDTO dto) {
        Grupo grupoExiste = grupoDAO.buscarPorId(id);
        if (grupoExiste == null)
            throw new IllegalArgumentException("Grupo não encontrado");

        validar(dto);

        // O DTO não carrega o id nem o status: o id vem da URL
        // e o status atual é preservado para o UPDATE não zerá-lo.
        Grupo grupo = GrupoMapper.dtoToEntity(dto);
        grupo.setIdGrupo(id);
        grupo.setStatusGrupo(grupoExiste.getStatusGrupo());
        grupoDAO.alterar(grupo);
    }

    public void excluir(int id) {
        if (grupoDAO.buscarPorId(id) == null)
            throw new IllegalArgumentException("Grupo não encontrado");

        grupoDAO.excluir(id);
    }

    private void validar(GrupoRequestDTO dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            System.out.println("ERRO: O nome do grupo é obrigatório");
            throw new IllegalArgumentException("O nome do grupo é obrigatório.");
        }
        if (dto.dataCriacao() == null) {
            System.out.println("ERRO: A data de criação é obrigatória");
            throw new IllegalArgumentException("A data de criação é obrigatória.");
        }
        if (dto.qtdUsuarios() < 0) {
            System.out.println("ERRO: A quantidade de usuários não pode ser negativa.");
            throw new IllegalArgumentException("A quantidade de usuários não pode ser negativa.");
        }

        // Anti-injection (SQL/HTML/path traversal) e whitelist de campos estruturados
        ValidadorEntrada.textoLivre("O nome do grupo", dto.nome(), 50);
    }
}