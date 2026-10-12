package br.com.fiap.greenlify.mapper;

import br.com.fiap.greenlify.dto.PagamentoContaRequestDTO;
import br.com.fiap.greenlify.dto.PagamentoContaResponseDTO;
import br.com.fiap.greenlify.models.ContaLuz;
import br.com.fiap.greenlify.models.PagamentoConta;
import br.com.fiap.greenlify.models.Usuario;

public class PagamentoContaMapper {
    public static PagamentoConta dtoToEntity (PagamentoContaRequestDTO dto){
        PagamentoConta pagamento = new PagamentoConta();
        pagamento.setDataPagamento(dto.dataPagamento());
        pagamento.setValorPago(dto.valorPago());
        pagamento.setUsuario(dto.usuario());
        pagamento.setContaLuz(dto.contaLuz());
        return pagamento;
    }

    public static PagamentoContaResponseDTO toDto (PagamentoConta pagamento){
        PagamentoContaResponseDTO dto = new PagamentoContaResponseDTO(
                pagamento.getDataPagamento(),
                pagamento.getValorPago(),
                pagamento.getUsuario(),
                pagamento.getContaLuz()
        );
        return dto;
    }
}