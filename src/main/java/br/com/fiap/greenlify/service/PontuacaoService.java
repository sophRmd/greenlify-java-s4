package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.enums.DificuldadeEnum;
import br.com.fiap.greenlify.enums.ImpactoEnum;
import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.PontuacaoDAO;
import br.com.fiap.greenlify.dto.PontuacaoRequestDTO;
import br.com.fiap.greenlify.dto.PontuacaoResponseDTO;
import br.com.fiap.greenlify.mapper.PontuacaoMapper;
import br.com.fiap.greenlify.models.Pontuacao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PontuacaoService {
    private final PontuacaoDAO pontuacaoDAO;

    public PontuacaoService() {
        this.pontuacaoDAO = new PontuacaoDAO();
    }

    public PontuacaoResponseDTO buscarPorId(int id) {
        Pontuacao pontuacao = pontuacaoDAO.buscarPorId(id);
        if (pontuacao == null) {
            throw new IllegalArgumentException("Pontuação não encontrada");
        }
        return PontuacaoMapper.toDto(pontuacao);
    }

    public PontuacaoResponseDTO buscarPorImpactoEDificuldade(String impacto, String dificuldade) {
        // Parâmetros vindos da URL: só aceita valores dos enums (whitelist).
        String impactoValido = ValidadorEntrada.enumValor(ImpactoEnum.class, "Impacto", impacto).name();
        String dificuldadeValida = ValidadorEntrada.enumValor(DificuldadeEnum.class, "Dificuldade", dificuldade).name();
        Pontuacao pontuacao = pontuacaoDAO.buscarPorImpactoEDificuldade(impactoValido, dificuldadeValida);
        if (pontuacao == null) {
            throw new IllegalArgumentException("Pontuação não encontrada");
        }
        return PontuacaoMapper.toDto(pontuacao);
    }

    public List<PontuacaoResponseDTO> listar() {
        return pontuacaoDAO.listar()
                .stream()
                .map(PontuacaoMapper::toDto)
                .toList();
    }

    public void cadastrar(PontuacaoRequestDTO dto) {
        validar(dto);

        if (pontuacaoDAO.buscarPorId(dto.idPontuacao()) != null) {
            System.out.println("ERRO: Já existe uma pontuação com esse id.");
            throw new IllegalArgumentException("Já existe uma pontuação com esse id.");
        }
        if (existeCombinacao(dto, null)) {
            System.out.println("ERRO: Já existe uma pontuação para essa combinação de impacto e dificuldade.");
            throw new IllegalArgumentException(
                    "Já existe uma pontuação para essa combinação de impacto e dificuldade.");
        }
        pontuacaoDAO.cadastrar(PontuacaoMapper.dtoToEntity(dto));
    }

    public void excluir(int id) {
        Pontuacao pontuacao = pontuacaoDAO.buscarPorId(id);
        if (pontuacao == null) {
            throw new IllegalArgumentException("Pontuação não encontrada");
        }
        pontuacaoDAO.excluir(id);
    }

    public void alterar(PontuacaoRequestDTO dto, int id) {
        Pontuacao existente = pontuacaoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Pontuação não encontrada");
        }
        if (dto.idPontuacao() != id) {
            System.out.println("ERRO: O id informado está incorreto.");
            throw new IllegalArgumentException("O id informado está incorreto.");
        }
        validar(dto);

        if (existeCombinacao(dto, id)) {
            System.out.println("ERRO: Já existe uma pontuação para essa combinação de impacto e dificuldade.");
            throw new IllegalArgumentException(
                    "Já existe uma pontuação para essa combinação de impacto e dificuldade.");
        }
        pontuacaoDAO.alterar(PontuacaoMapper.dtoToEntity(dto));
    }

    private boolean existeCombinacao(PontuacaoRequestDTO dto, Integer idIgnorado) {
        Pontuacao outra = pontuacaoDAO.buscarPorImpactoEDificuldade(
                dto.impacto().toString(), dto.dificuldade().toString());
        return outra != null && (idIgnorado == null || outra.getIdPontuacao() != idIgnorado);
    }

    private void validar(PontuacaoRequestDTO dto) {
        if (dto.idPontuacao() <= 0) {
            System.out.println("ERRO: O id da pontuação deve ser maior que zero.");
            throw new IllegalArgumentException("O id da pontuação deve ser maior que zero.");
        }
        if (dto.impacto() == null) {
            System.out.println("ERRO: O impacto é obrigatório.");
            throw new IllegalArgumentException("O impacto é obrigatório.");
        }
        if (dto.dificuldade() == null) {
            System.out.println("ERRO: A dificuldade é obrigatória.");
            throw new IllegalArgumentException("A dificuldade é obrigatória.");
        }
    }
}