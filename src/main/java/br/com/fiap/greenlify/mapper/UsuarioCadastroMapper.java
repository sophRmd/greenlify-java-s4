package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.UsuarioCadastroRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioCadastroResponseDTO;
import br.com.fiap.greenlify.models.UsuarioComum;

public class UsuarioCadastroMapper {
    public static UsuarioComum dtoToEntity (UsuarioCadastroRequestDTO dto){
        UsuarioComum usuario = new UsuarioComum();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setNivelAcesso(dto.nivelAcesso());
        usuario.setStatusConta(dto.statusConta());
        usuario.setCpf(dto.cpf());
        usuario.setDataNascimento(dto.dataNascimento());
        usuario.setGrupo(dto.grupo());
        return usuario;
    }

    public static UsuarioCadastroResponseDTO toDto (UsuarioComum usuario){
        UsuarioCadastroResponseDTO dto = new UsuarioCadastroResponseDTO(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getNivelAcesso(),
                usuario.getStatusConta(),
                usuario.getCpf(),
                usuario.getDataNascimento(),
                usuario.getGrupo()
        );
        return dto;
    }
}
