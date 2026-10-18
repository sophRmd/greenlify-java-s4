package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.GrupoDAO;
import br.com.fiap.greenlify.dto.GrupoRequestDTO;
import br.com.fiap.greenlify.dto.GrupoResponseDTO;
import br.com.fiap.greenlify.mapper.GrupoMapper;
import br.com.fiap.greenlify.models.Grupo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoService {

    // Status atribuído a todo grupo novo, já que o DTO de cadastro não o informa.
    // Ajuste para o valor que representa "ativo" no seu banco.
    private static final int STATUS_INICIAL = 1;

    private final GrupoDAO grupoDAO;

    public GrupoService() {
        this.grupoDAO = new GrupoDAO();
    }

    public List<GrupoResponseDTO> listar() {
        return grupoDAO.listar()
                .stream()
                .map(GrupoMapper::toDto)
                .toList();
    }

    public GrupoResponseDTO buscarPorId(int id) {
        var grupo = grupoDAO.buscarPorId(id);
        if (grupo == null)
            throw new IllegalArgumentException("Grupo não encontrado");
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