package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.ContaLuz;
import br.com.fiap.greenlify.models.Usuario;

import java.time.LocalDate;

public record PagamentoContaRequestDTO(
        LocalDate dataPagamento,
        double valorPago,
        Usuario usuario,
        ContaLuz contaLuz
) {
}