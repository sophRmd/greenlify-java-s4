package br.com.fiap.greenlify.dto;

public record UsuarioMissaoRequestDTO(
        int tentativas,
        int progressoAtual,
        int idUsuario,
        int idMissao
) {
}
