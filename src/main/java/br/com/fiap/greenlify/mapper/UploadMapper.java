package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.UploadRequestDTO;
import br.com.fiap.greenlify.dto.UploadResponseDTO;
import br.com.fiap.greenlify.models.Upload;

public class UploadMapper {
    public static Upload dtoToEntity (UploadRequestDTO dto){
        Upload upload = new Upload();
        upload.setStatus(dto.status());
        upload.setDataEnvio(dto.dataEnvio());
        upload.setDataAnalise(dto.dataAnalise());
        upload.setArquivo(dto.arquivo());
        upload.setAnalise(dto.analise());
        upload.setConfiancaDuplicidade(dto.confiancaDuplicidade());
        upload.setMissao(dto.missao());
        upload.setUsuario(dto.usuario());
        return upload;
    }

    public static UploadResponseDTO toDto (Upload upload){
        UploadResponseDTO dto = new UploadResponseDTO(
                upload.getStatus(),
                upload.getDataEnvio(),
                upload.getDataAnalise(),
                upload.getArquivo(),
                upload.getAnalise(),
                upload.getConfiancaDuplicidade(),
                upload.getMissao(),
                upload.getUsuario()
        );
        return dto;
    }
}