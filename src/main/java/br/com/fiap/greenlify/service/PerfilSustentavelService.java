package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.PerfilSustentavelDAO;
import br.com.fiap.greenlify.dto.PerfilSustentavelRequestDTO;
import br.com.fiap.greenlify.dto.PerfilSustentavelResponseDTO;
import br.com.fiap.greenlify.mapper.PerfilSustentavelMapper;
import br.com.fiap.greenlify.models.PerfilSustentavel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PerfilSustentavelService {
    private final PerfilSustentavelDAO perfilDAO;

    public PerfilSustentavelService() {
        this.perfilDAO = new PerfilSustentavelDAO();
    }

    public PerfilSustentavelResponseDTO buscarPorId(int id) {
        PerfilSustentavel perfil = perfilDAO.buscarPorId(id);
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil sustentável não encontrado");
        }
        return PerfilSustentavelMapper.toDto(perfil);
    }

    public List<PerfilSustentavelResponseDTO> listar() {
        return perfilDAO.listar()
                .stream()
                .map(PerfilSustentavelMapper::toDto)
                .toList();
    }

    public void cadastrar(PerfilSustentavelRequestDTO dto) {
        validar(dto);
        perfilDAO.cadastrar(PerfilSustentavelMapper.dtoToEntity(dto));
    }

    public void excluir(int id) {
        PerfilSustentavel perfil = perfilDAO.buscarPorId(id);
        if (perfil == null) {
            throw new IllegalArgumentException("Perfil sustentável não encontrado");
        }
        perfilDAO.excluir(id);
    }

    public void alterar(PerfilSustentavelRequestDTO dto, int id) {
        PerfilSustentavel existente = perfilDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Perfil sustentável não encontrado");
        }
        if (dto.idPerfilSustentavel() != id) {
            System.out.println("ERRO: O id informado está incorreto.");
            throw new IllegalArgumentException("O id informado está incorreto.");
        }
        validar(dto);
        perfilDAO.alterar(PerfilSustentavelMapper.dtoToEntity(dto));
    }

    private void validar(PerfilSustentavelRequestDTO dto) {
        if (dto.usuario() == null) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.nivel() == null) {
            System.out.println("ERRO: O nível de contribuição é obrigatório.");
            throw new IllegalArgumentException("O nível de contribuição é obrigatório.");
        }
        if (dto.xpSustentavel() < 0) {
            System.out.println("ERRO: O XP não pode ser negativo.");
            throw new IllegalArgumentException("O XP não pode ser negativo.");
        }
        if (dto.totalMissoesConcluidas() < 0) {
            System.out.println("ERRO: O total de missões concluídas não pode ser negativo.");
            throw new IllegalArgumentException("O total de missões concluídas não pode ser negativo.");
        }
        if (dto.totalAguaEconomizada() < 0
                || dto.totalEnergiaEconomizada() < 0
                || dto.totalCarbonoEconomizado() < 0) {
            System.out.println("ERRO: Os totais economizados não podem ser negativos.");
            throw new IllegalArgumentException("Os totais economizados não podem ser negativos.");
        }
    }
}