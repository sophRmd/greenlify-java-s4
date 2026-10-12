package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.UsuarioSeloRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioSeloResponseDTO;
import br.com.fiap.greenlify.models.UsuarioSelo;

public class UsuarioSeloMapper {
    public static UsuarioSelo dtoToEntity (UsuarioSeloRequestDTO dto){
        UsuarioSelo usuarioSelo = new UsuarioSelo();
        usuarioSelo.setDataColeta(dto.dataColeta());
        usuarioSelo.setUsuario(dto.usuario());
        usuarioSelo.setSelo(dto.selo());
        return usuarioSelo;
    }

    public static UsuarioSeloResponseDTO toDto (UsuarioSelo usuarioSelo){
        UsuarioSeloResponseDTO dto = new UsuarioSeloResponseDTO(
                usuarioSelo.getDataColeta(),
                usuarioSelo.getUsuario(),
                usuarioSelo.getSelo()
        );
        return dto;
    }
}