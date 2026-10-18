package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.SeloDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dao.UsuarioSeloDAO;
import br.com.fiap.greenlify.dto.UsuarioSeloRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioSeloResponseDTO;
import br.com.fiap.greenlify.mapper.UsuarioSeloMapper;
import br.com.fiap.greenlify.models.UsuarioSelo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioSeloService {
    private final UsuarioSeloDAO usuarioSeloDAO;
    private final UsuarioComumDAO usuarioDAO;
    private final SeloDAO seloDAO;

    public UsuarioSeloService() {
        this.usuarioSeloDAO = new UsuarioSeloDAO();
        this.usuarioDAO = new UsuarioComumDAO();
        this.seloDAO = new SeloDAO();
    }

    public UsuarioSeloResponseDTO buscarPorId(int id) {
        UsuarioSelo usuarioSelo = usuarioSeloDAO.buscarPorId(id);
        if (usuarioSelo == null) {
            throw new IllegalArgumentException("Coleta de selo não encontrada");
        }
        return UsuarioSeloMapper.toDto(usuarioSelo);
    }

    public List<UsuarioSeloResponseDTO> listar() {
        return usuarioSeloDAO.listar()
                .stream()
                .map(UsuarioSeloMapper::toDto)
                .toList();
    }

    public List<UsuarioSeloResponseDTO> listarPorUsuario(int idUsuario) {
        return usuarioSeloDAO.buscarPorIdUsuario(idUsuario)
                .stream()
                .map(UsuarioSeloMapper::toDto)
                .toList();
    }

    public void cadastrar(UsuarioSeloRequestDTO dto) {
        validar(dto);

        boolean jaColetou = usuarioSeloDAO.listar()
                .stream()
                .anyMatch(us -> us.getUsuario() != null
                        && us.getSelo() != null
                        && us.getUsuario().getIdUsuario() == dto.idUsuario()
                        && us.getSelo().getIdSelo() == dto.idSelo());
        if (jaColetou) {
            System.out.println("ERRO: O usuário já coletou esse selo.");
            throw new IllegalArgumentException("O usuário já coletou esse selo.");
        }

        usuarioSeloDAO.cadastrar(UsuarioSeloMapper.dtoToEntity(dto));
    }

    public void excluir(int id) {
        UsuarioSelo usuarioSelo = usuarioSeloDAO.buscarPorId(id);
        if (usuarioSelo == null) {
            throw new IllegalArgumentException("Coleta de selo não encontrada");
        }
        usuarioSeloDAO.excluir(id);
    }

    public void alterar(UsuarioSeloRequestDTO dto, int id) {
        UsuarioSelo existente = usuarioSeloDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Coleta de selo não encontrada");
        }
        validar(dto);

        UsuarioSelo usuarioSelo = UsuarioSeloMapper.dtoToEntity(dto);
        usuarioSelo.setIdUsuarioSelo(id);
        usuarioSeloDAO.alterar(usuarioSelo);
    }

    private void validar(UsuarioSeloRequestDTO dto) {
        if (dto.dataColeta() == null) {
            System.out.println("ERRO: A data de coleta é obrigatória.");
            throw new IllegalArgumentException("A data de coleta é obrigatória.");
        }
        if (dto.dataColeta().isAfter(LocalDate.now())) {
            System.out.println("ERRO: A data de coleta não pode ser futura.");
            throw new IllegalArgumentException("A data de coleta não pode ser futura.");
        }
        if (dto.idUsuario() <= 0) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.idSelo() <= 0) {
            System.out.println("ERRO: O selo é obrigatório.");
            throw new IllegalArgumentException("O selo é obrigatório.");
        }
        if (usuarioDAO.buscarPorId(dto.idUsuario()) == null) {
            System.out.println("ERRO: Usuário informado não existe.");
            throw new IllegalArgumentException("O usuário informado não existe.");
        }
        if (seloDAO.buscarPorId(dto.idSelo()) == null) {
            System.out.println("ERRO: Selo informado não existe.");
            throw new IllegalArgumentException("O selo informado não existe.");
        }
    }
}