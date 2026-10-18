package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dao.UsuarioMissaoDAO;
import br.com.fiap.greenlify.dto.UsuarioMissaoRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioMissaoResponseDTO;
import br.com.fiap.greenlify.mapper.UsuarioMissaoMapper;
import br.com.fiap.greenlify.models.UsuarioMissao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioMissaoService {
    private final UsuarioMissaoDAO usuarioMissaoDAO;
    private final UsuarioComumDAO usuarioDAO;

    public UsuarioMissaoService() {
        this.usuarioMissaoDAO = new UsuarioMissaoDAO();
        this.usuarioDAO = new UsuarioComumDAO();
    }

    public UsuarioMissaoResponseDTO buscarPorId(int id) {
        UsuarioMissao usuarioMissao = usuarioMissaoDAO.buscarPorId(id);
        if (usuarioMissao == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        return UsuarioMissaoMapper.toDto(usuarioMissao);
    }

    public List<UsuarioMissaoResponseDTO> listar() {
        return usuarioMissaoDAO.listar()
                .stream()
                .map(UsuarioMissaoMapper::toDto)
                .toList();
    }

    public List<UsuarioMissaoResponseDTO> listarPorUsuario(int idUsuario) {
        return usuarioMissaoDAO.listarPorUsuario(idUsuario)
                .stream()
                .map(UsuarioMissaoMapper::toDto)
                .toList();
    }

    public void cadastrar(UsuarioMissaoRequestDTO dto) {
        validar(dto);

        boolean jaTemMissao = usuarioMissaoDAO.listarPorUsuario(dto.idUsuario())
                .stream()
                .anyMatch(um -> um.getMissao().getIdMissao() == dto.idMissao());
        if (jaTemMissao) {
            System.out.println("ERRO: O usuário já possui essa missão.");
            throw new IllegalArgumentException("O usuário já possui essa missão.");
        }

        usuarioMissaoDAO.cadastrar(UsuarioMissaoMapper.dtoToEntity(dto));
    }

    public void excluir(int id) {
        UsuarioMissao usuarioMissao = usuarioMissaoDAO.buscarPorId(id);
        if (usuarioMissao == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        usuarioMissaoDAO.excluir(id);
    }

    public void alterar(UsuarioMissaoRequestDTO dto, int id) {
        UsuarioMissao existente = usuarioMissaoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        validar(dto);

        UsuarioMissao usuarioMissao = UsuarioMissaoMapper.dtoToEntity(dto);
        usuarioMissao.setIdUsuarioMissao(id);
        usuarioMissaoDAO.alterar(usuarioMissao);
    }

    private void validar(UsuarioMissaoRequestDTO dto) {
        if (dto.tentativas() < 0) {
            System.out.println("ERRO: O número de tentativas não pode ser negativo.");
            throw new IllegalArgumentException("O número de tentativas não pode ser negativo.");
        }
        if (dto.progressoAtual() < 0) {
            System.out.println("ERRO: O progresso atual não pode ser negativo.");
            throw new IllegalArgumentException("O progresso atual não pode ser negativo.");
        }
        if (dto.idUsuario() <= 0) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.idMissao() <= 0) {
            System.out.println("ERRO: A missão é obrigatória.");
            throw new IllegalArgumentException("A missão é obrigatória.");
        }
        if (usuarioDAO.buscarPorId(dto.idUsuario()) == null) {
            System.out.println("ERRO: Usuário informado não existe.");
            throw new IllegalArgumentException("O usuário informado não existe.");
        }
    }
}