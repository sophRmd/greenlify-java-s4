package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.CarteiraDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.CarteiraRequestDTO;
import br.com.fiap.greenlify.dto.CarteiraResponseDTO;
import br.com.fiap.greenlify.mapper.CarteiraMapper;
import br.com.fiap.greenlify.models.Carteira;
import br.com.fiap.greenlify.models.UsuarioComum;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarteiraService {

    private final CarteiraDAO carteiraDAO;
    private final UsuarioComumDAO usuarioDAO;

    public CarteiraService() {
        this.carteiraDAO = new CarteiraDAO();
        this.usuarioDAO = new UsuarioComumDAO();
    }

    public List<CarteiraResponseDTO> listar() {
        return carteiraDAO.listar()
                .stream()
                .map(CarteiraMapper::toDto)
                .toList();
    }

    public CarteiraResponseDTO buscarPorId(int id) {
        var carteira = carteiraDAO.buscarPorId(id);
        if (carteira == null)
            throw new IllegalArgumentException("Carteira não encontrada");
        return CarteiraMapper.toDto(carteira);
    }

    public CarteiraResponseDTO buscarPorIdUsuario(int idUsuario) {
        var carteira = carteiraDAO.buscarPorIdUsuario(idUsuario);
        if (carteira == null)
            throw new IllegalArgumentException("Carteira não encontrada");
        return CarteiraMapper.toDto(carteira);
    }

    public void cadastrar(int idUsuario, CarteiraRequestDTO dto) {
        if (dto.qtdPontos() < 0) {
            System.out.println("ERRO: A quantidade de pontos não pode ser negativa.");
            throw new IllegalArgumentException("A quantidade de pontos não pode ser negativa.");
        }
        if (dto.valorReal() < 0) {
            System.out.println("ERRO: O valor em reais não pode ser negativo.");
            throw new IllegalArgumentException("O valor em reais não pode ser negativo.");
        }

        UsuarioComum usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            System.out.println("ERRO: Usuário não encontrado");
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        if (carteiraDAO.buscarPorIdUsuario(idUsuario) != null) {
            System.out.println("ERRO: Usuário já possui carteira");
            throw new IllegalArgumentException("Usuário já possui carteira.");
        }

        Carteira carteira = CarteiraMapper.dtoToEntity(dto);
        carteira.setUsuario(usuario);
        carteiraDAO.cadastrar(carteira);
    }

    public void atualizar(int id, CarteiraRequestDTO dto) {
        if (carteiraDAO.buscarPorId(id) == null)
            throw new IllegalArgumentException("Carteira não encontrada");

        if (dto.qtdPontos() < 0 || dto.valorReal() < 0) {
            System.out.println("ERRO: Pontos e valor não podem ser negativos.");
            throw new IllegalArgumentException("Pontos e valor não podem ser negativos.");
        }

        Carteira carteira = CarteiraMapper.dtoToEntity(dto);
        carteira.setIdCarteira(id);
        carteiraDAO.alterar(carteira);
    }

    public void excluir(int id) {
        if (carteiraDAO.buscarPorId(id) == null)
            throw new IllegalArgumentException("Carteira não encontrada");

        carteiraDAO.excluir(id);
    }
}