package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.enums.StatusUploadEnum;
import br.com.fiap.greenlify.models.Missao;
import br.com.fiap.greenlify.models.UsuarioComum;

import java.time.LocalDate;

public record UploadResponseDTO(
        StatusUploadEnum status,
        LocalDate dataEnvio,
        LocalDate dataAnalise,
        String arquivo,
        String analise,
        int confiancaDuplicidade,
        Missao missao,
        UsuarioComum usuario
) {
}