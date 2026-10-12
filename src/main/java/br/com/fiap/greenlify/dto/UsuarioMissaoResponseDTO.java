package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.Missao;
import br.com.fiap.greenlify.models.Usuario;

public record UsuarioMissaoResponseDTO(
        int tentativas,
        int progressoAtual,
        Usuario usuario,
        Missao missao
) {
}