package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.CarteiraRequestDTO;
import br.com.fiap.greenlify.dto.CarteiraResponseDTO;
import br.com.fiap.greenlify.models.Carteira;

public class CarteiraMapper {
    public static Carteira dtoToEntity(CarteiraRequestDTO dto){
        Carteira carteira = new Carteira();
        carteira.setQtdPontos(dto.qtdPontos());
        carteira.setValorReal(dto.valorReal());
        return carteira;
    }

    public static CarteiraResponseDTO toDto (Carteira carteira){
        return new CarteiraResponseDTO(
                carteira.getIdCarteira(),
                carteira.getQtdPontos(),
                carteira.getValorReal()
        );
    }
}
