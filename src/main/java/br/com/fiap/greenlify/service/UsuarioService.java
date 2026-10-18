package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.GrupoDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.UsuarioCadastroRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioCadastroResponseDTO;
import br.com.fiap.greenlify.enums.StatusContaEnum;
import br.com.fiap.greenlify.mapper.UsuarioCadastroMapper;
import br.com.fiap.greenlify.models.UsuarioComum;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioComumDAO usuarioDAO;
    private final GrupoDAO grupoDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioComumDAO();
        this.grupoDAO = new GrupoDAO();
    }

    public UsuarioCadastroResponseDTO buscarPorId(int id) {
        UsuarioComum usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        return UsuarioCadastroMapper.toDto(usuario);
    }

    public List<UsuarioCadastroResponseDTO> listar() {
        return usuarioDAO.listar()
                .stream()
                .map(UsuarioCadastroMapper::toDto)
                .toList();
    }

    public void cadastrar(UsuarioCadastroRequestDTO dto) {
        validar(dto);

        UsuarioComum usuario = UsuarioCadastroMapper.dtoToEntity(dto);
        usuario.setNivelAcesso(0); // este serviço só cadastra usuários comuns
        usuarioDAO.cadastrar(usuario);
    }

    public void excluir(int id) {
        UsuarioComum usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        usuarioDAO.excluir(id);
    }

    public void alterar(UsuarioCadastroRequestDTO dto, int id) {
        UsuarioComum existente = usuarioDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Usuário não encontrado");
        }
        validar(dto);

        UsuarioComum usuario = UsuarioCadastroMapper.dtoToEntity(dto);
        usuario.setIdUsuario(id);
        usuario.setNivelAcesso(0);
        usuarioDAO.alterar(usuario);
    }

    public UsuarioCadastroResponseDTO login(String email, String senha) {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("E-mail e senha são obrigatórios.");
        }
        ValidadorEntrada.email(email);
        ValidadorEntrada.senha(senha);
        UsuarioComum usuario = usuarioDAO.buscarPorEmailESenha(email, senha);
        if (usuario == null) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }
        if (usuario.getStatusConta() != StatusContaEnum.ATIVA) {
            throw new IllegalArgumentException("Conta não está ativa.");
        }
        return UsuarioCadastroMapper.toDto(usuario);
    }

    private void validar(UsuarioCadastroRequestDTO dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            System.out.println("ERRO: Nome é obrigatório");
            throw new IllegalArgumentException("Nome é obrigatório.");
        }
        if (dto.email() == null || !dto.email().contains("@")) {
            System.out.println("ERRO: E-mail inválido");
            throw new IllegalArgumentException("E-mail inválido.");
        }
        if (dto.senha() == null || dto.senha().isBlank()) {
            System.out.println("ERRO: Senha é obrigatória");
            throw new IllegalArgumentException("Senha é obrigatória.");
        }
        if (dto.cpf() == null || dto.cpf().replaceAll("\\D", "").length() != 11) {
            System.out.println("ERRO: CPF inválido");
            throw new IllegalArgumentException("CPF inválido: deve ter 11 dígitos.");
        }
        if (dto.dataNascimento() == null || !dto.dataNascimento().isBefore(LocalDate.now())) {
            System.out.println("ERRO: Data de nascimento inválida");
            throw new IllegalArgumentException("A data de nascimento deve ser anterior a hoje.");
        }
        if (dto.statusConta() == null) {
            System.out.println("ERRO: O status da conta é obrigatório");
            throw new IllegalArgumentException("O status da conta é obrigatório.");
        }
        if (dto.idGrupo() != null && grupoDAO.buscarPorId(dto.idGrupo()) == null) {
            System.out.println("ERRO: Grupo informado não existe");
            throw new IllegalArgumentException("O grupo informado não existe.");
        }

        // Anti-injection (SQL/HTML/path traversal) e whitelist de campos estruturados
        ValidadorEntrada.nomePessoa("Nome", dto.nome(), 80);
        ValidadorEntrada.email(dto.email());
        ValidadorEntrada.senha(dto.senha());
        ValidadorEntrada.cpf(dto.cpf());
    }
}