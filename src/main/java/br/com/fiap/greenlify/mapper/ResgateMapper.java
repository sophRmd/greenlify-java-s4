package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.ResgateRequestDTO;
import br.com.fiap.greenlify.dto.ResgateResponseDTO;
import br.com.fiap.greenlify.models.Resgate;

public class ResgateMapper {
    public static Resgate dtoToEntity (ResgateRequestDTO dto){
        Resgate resgate = new Resgate();
        resgate.setDataConcessao(dto.dataConcessao());
        resgate.setUsuario(dto.usuario());
        resgate.setRecompensa(dto.recompensa());
        return resgate;
    }

    public static ResgateResponseDTO toDto (Resgate resgate){
        ResgateResponseDTO dto = new ResgateResponseDTO(
                resgate.getIdResgate(),
                resgate.getDataConcessao(),
                resgate.getUsuario(),
                resgate.getRecompensa()
        );
        return dto;
    }
}