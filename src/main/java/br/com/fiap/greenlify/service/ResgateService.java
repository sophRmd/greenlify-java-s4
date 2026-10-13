package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.RecompensaDAO;
import br.com.fiap.greenlify.dao.ResgateDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.ResgateRequestDTO;
import br.com.fiap.greenlify.dto.ResgateResponseDTO;
import br.com.fiap.greenlify.mapper.ResgateMapper;
import br.com.fiap.greenlify.models.Resgate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResgateService {
    private final ResgateDAO resgateDAO;
    private final UsuarioComumDAO usuarioDAO;
    private final RecompensaDAO recompensaDAO;

    public ResgateService() {
        this.resgateDAO = new ResgateDAO();
        this.usuarioDAO = new UsuarioComumDAO();
        this.recompensaDAO = new RecompensaDAO();
    }

    public ResgateResponseDTO buscarPorId(int id) {
        Resgate resgate = resgateDAO.buscarPorId(id);
        if (resgate == null) {
            throw new IllegalArgumentException("Resgate não encontrado");
        }
        return ResgateMapper.toDto(resgate);
    }

    public List<ResgateResponseDTO> listar() {
        return resgateDAO.listar()
                .stream()
                .map(ResgateMapper::toDto)
                .toList();
    }

    public void cadastrar(ResgateRequestDTO dto) {
        validar(dto);
        resgateDAO.cadastrar(ResgateMapper.dtoToEntity(dto));
    }

    public void excluir(int id) {
        Resgate resgate = resgateDAO.buscarPorId(id);
        if (resgate == null) {
            throw new IllegalArgumentException("Resgate não encontrado");
        }
        resgateDAO.excluir(id);
    }

    public void alterar(ResgateRequestDTO dto, int id) {
        Resgate existente = resgateDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Resgate não encontrado");
        }
        validar(dto);

        Resgate resgate = ResgateMapper.dtoToEntity(dto);
        resgate.setIdResgate(id);
        resgateDAO.alterar(resgate);
    }

    private void validar(ResgateRequestDTO dto) {
        if (dto.dataConcessao() == null) {
            System.out.println("ERRO: A data de concessão é obrigatória.");
            throw new IllegalArgumentException("A data de concessão é obrigatória.");
        }
        if (dto.usuario() == null) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.recompensa() == null) {
            System.out.println("ERRO: A recompensa é obrigatória.");
            throw new IllegalArgumentException("A recompensa é obrigatória.");
        }
        if (usuarioDAO.buscarPorId(dto.usuario().getIdUsuario()) == null) {
            System.out.println("ERRO: Usuário informado não existe.");
            throw new IllegalArgumentException("O usuário informado não existe.");
        }
        if (recompensaDAO.buscarPorId(dto.recompensa().getIdRecompensa()) == null) {
            System.out.println("ERRO: Recompensa informada não existe.");
            throw new IllegalArgumentException("A recompensa informada não existe.");
        }
    }
}