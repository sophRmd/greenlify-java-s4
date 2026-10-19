package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dao.MissaoMensalDAO;
import br.com.fiap.greenlify.dao.UsuarioMissaoDAO;
import br.com.fiap.greenlify.dto.UsuarioMissaoRequestDTO;
import br.com.fiap.greenlify.dto.UsuarioMissaoResponseDTO;
import br.com.fiap.greenlify.mapper.UsuarioMissaoMapper;
import br.com.fiap.greenlify.models.MissaoMensal;
import br.com.fiap.greenlify.models.UsuarioMissao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UsuarioMissaoService {
    private final UsuarioMissaoDAO usuarioMissaoDAO;
    private final UsuarioComumDAO usuarioDAO;
    private final MissaoMensalDAO missaoMensalDAO;
    private final StreakService streakService;
    private final RankingAllTimeService rankingAllTimeService;

    public UsuarioMissaoService() {
        this.usuarioMissaoDAO = new UsuarioMissaoDAO();
        this.usuarioDAO = new UsuarioComumDAO();
        this.missaoMensalDAO = new MissaoMensalDAO();
        this.streakService = new StreakService();
        this.rankingAllTimeService = new RankingAllTimeService();
    }

    public UsuarioMissaoResponseDTO buscarPorId(int id) {
        UsuarioMissao usuarioMissao = usuarioMissaoDAO.buscarPorId(id);
        if (usuarioMissao == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        return UsuarioMissaoMapper.toDto(usuarioMissao);
    }

    public List<UsuarioMissaoResponseDTO> listar() {
        return usuarioMissaoDAO.listar()
                .stream()
                .map(UsuarioMissaoMapper::toDto)
                .toList();
    }

    public List<UsuarioMissaoResponseDTO> listarPorUsuario(int idUsuario) {
        return usuarioMissaoDAO.listarPorUsuario(idUsuario)
                .stream()
                .map(UsuarioMissaoMapper::toDto)
                .toList();
    }

    public void cadastrar(UsuarioMissaoRequestDTO dto) {
        validar(dto);

        boolean jaTemMissao = usuarioMissaoDAO.listarPorUsuario(dto.idUsuario())
                .stream()
                .anyMatch(um -> um.getMissao().getIdMissao() == dto.idMissao());
        if (jaTemMissao) {
            System.out.println("ERRO: O usuário já possui essa missão.");
            throw new IllegalArgumentException("O usuário já possui essa missão.");
        }

        UsuarioMissao novo = UsuarioMissaoMapper.dtoToEntity(dto);
        usuarioMissaoDAO.cadastrar(novo);
        registrarConclusaoMissaoMensal(dto, new UsuarioMissao(), novo.getIdUsuarioMissao()); // progresso anterior = 0
    }

    public void excluir(int id) {
        UsuarioMissao usuarioMissao = usuarioMissaoDAO.buscarPorId(id);
        if (usuarioMissao == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        usuarioMissaoDAO.excluir(id);
    }

    public void alterar(UsuarioMissaoRequestDTO dto, int id) {
        UsuarioMissao existente = usuarioMissaoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        validar(dto);

        UsuarioMissao usuarioMissao = UsuarioMissaoMapper.dtoToEntity(dto);
        usuarioMissao.setIdUsuarioMissao(id);
        usuarioMissaoDAO.alterar(usuarioMissao);
        registrarConclusaoMissaoMensal(dto, existente, id);
    }

    /**
     * Quando o progresso de uma missão mensal atinge a meta, grava a data de
     * conclusão (rankings) e avisa o ranking all time; se isso aconteceu dentro
     * do período da missão, registra também a atividade do mês no streak.
     */
    private void registrarConclusaoMissaoMensal(UsuarioMissaoRequestDTO dto, UsuarioMissao anterior,
                                                int idUsuarioMissao) {
        MissaoMensal mensal = missaoMensalDAO.buscarPorId(dto.idMissao());
        if (mensal == null) {
            return;
        }
        LocalDate hoje = LocalDate.now();
        boolean noPeriodo = mensal.getDataInicio() != null && mensal.getDataFim() != null
                && !hoje.isBefore(mensal.getDataInicio()) && !hoje.isAfter(mensal.getDataFim());
        boolean acabouDeConcluir = anterior.getProgressoAtual() < mensal.getMeta()
                && dto.progressoAtual() >= mensal.getMeta();
        if (acabouDeConcluir) {
            usuarioMissaoDAO.registrarConclusao(idUsuarioMissao, hoje);
            rankingAllTimeService.registrarMissaoConcluida(dto.idUsuario());
        }
        if (noPeriodo && acabouDeConcluir) {
            streakService.registrarAtividade(dto.idUsuario(), hoje);
        }
    }

    private void validar(UsuarioMissaoRequestDTO dto) {
        if (dto.tentativas() < 0) {
            System.out.println("ERRO: O número de tentativas não pode ser negativo.");
            throw new IllegalArgumentException("O número de tentativas não pode ser negativo.");
        }
        if (dto.progressoAtual() < 0) {
            System.out.println("ERRO: O progresso atual não pode ser negativo.");
            throw new IllegalArgumentException("O progresso atual não pode ser negativo.");
        }
        if (dto.idUsuario() <= 0) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.idMissao() <= 0) {
            System.out.println("ERRO: A missão é obrigatória.");
            throw new IllegalArgumentException("A missão é obrigatória.");
        }
        if (usuarioDAO.buscarPorId(dto.idUsuario()) == null) {
            System.out.println("ERRO: Usuário informado não existe.");
            throw new IllegalArgumentException("O usuário informado não existe.");
        }
        // O DAO nunca devolve null: se o id não existe, retorna um objeto com os valores padrão.
        if (missaoMensalDAO.buscarPorId(dto.idMissao()).getIdMissao() != dto.idMissao()) {
            System.out.println("ERRO: Missão informada não existe.");
            throw new IllegalArgumentException("A missão informada não existe.");
        }
    }
}
