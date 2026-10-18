package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.enums.StatusUploadEnum;
import java.time.LocalDate;

public record UploadRequestDTO(
        StatusUploadEnum status,
        LocalDate dataEnvio,
        LocalDate dataAnalise,
        String arquivo,
        String analise,
        int confiancaDuplicidade,
        int idMissao,
        int idUsuario
) {
}
