package br.com.fiap.greenlify.dto;

import br.com.fiap.greenlify.models.NivelContribuicao;
import br.com.fiap.greenlify.models.Usuario;
import br.com.fiap.greenlify.models.UsuarioComum;

public record PerfilSustentavelRequestDTO(
        int idPerfilSustentavel,
        int xpSustentavel,
        int totalMissoesConcluidas,
        double totalAguaEconomizada,
        double totalEnergiaEconomizada,
        double totalCarbonoEconomizado,
        UsuarioComum usuario,
        NivelContribuicao nivel
) {
}