package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.enums.StatusContaEnum;
import br.com.fiap.greenlify.models.Grupo;

import java.time.LocalDate;

public record UsuarioCadastroResponseDTO(
        String nome,
        String email,
        String senha,
        int nivelAcesso,
        StatusContaEnum statusConta,
        String cpf,
        LocalDate dataNascimento,
        Grupo grupo
) {
}
