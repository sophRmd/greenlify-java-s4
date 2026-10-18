package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.UsuarioMissaoRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioMissaoResponseDTO;
import br.com.fiap.greenlify.models.UsuarioMissao;

public class UsuarioMissaoMapper {
    public static UsuarioMissao dtoToEntity (UsuarioMissaoRequestDTO dto){
        UsuarioMissao usuarioMissao = new UsuarioMissao();
        usuarioMissao.setTentativas(dto.tentativas());
        usuarioMissao.setProgressoAtual(dto.progressoAtual());
        usuarioMissao.setUsuario(ReferenciaMapper.usuario(dto.idUsuario()));
        usuarioMissao.setMissao(ReferenciaMapper.missao(dto.idMissao()));
        return usuarioMissao;
    }

    public static UsuarioMissaoResponseDTO toDto (UsuarioMissao usuarioMissao){
        UsuarioMissaoResponseDTO dto = new UsuarioMissaoResponseDTO(
                usuarioMissao.getIdUsuarioMissao(),
                usuarioMissao.getTentativas(),
                usuarioMissao.getProgressoAtual(),
                UsuarioResumoMapper.toDto(usuarioMissao.getUsuario()),
                usuarioMissao.getMissao()
        );
        return dto;
    }
}