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

    /** Inicia a missão para o usuário autenticado: tentativas e progresso sempre começam em 0. */
    public void cadastrar(UsuarioMissaoRequestDTO dto, int idUsuario) {
        validar(dto, idUsuario);

        boolean jaTemMissao = usuarioMissaoDAO.listarPorUsuario(idUsuario)
                .stream()
                .anyMatch(um -> um.getMissao().getIdMissao() == dto.idMissao());
        if (jaTemMissao) {
            System.out.println("ERRO: O usuário já possui essa missão.");
            throw new IllegalArgumentException("O usuário já possui essa missão.");
        }

        UsuarioMissao novo = UsuarioMissaoMapper.dtoToEntity(dto, idUsuario);
        usuarioMissaoDAO.cadastrar(novo);
    }

    public void excluir(int id) {
        UsuarioMissao usuarioMissao = usuarioMissaoDAO.buscarPorId(id);
        if (usuarioMissao == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        usuarioMissaoDAO.excluir(id);
    }

    /**
     * Registra um passo de progresso em uma missão mensal do usuário autenticado. O servidor é
     * quem avança o progresso (um passo por chamada, só dentro do período da missão) e quem
     * decide a conclusão; só então atualiza o ranking geral e o streak.
     *
     * @throws SecurityException        se o registro pertence a outro usuário
     * @throws IllegalArgumentException se o registro não existe, a missão está fora do período ou já foi concluída
     */
    public UsuarioMissaoResponseDTO registrarProgresso(int id, int idUsuario) {
        UsuarioMissao existente = usuarioMissaoDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Registro de usuário e missão não encontrado");
        }
        if (existente.getUsuario().getIdUsuario() != idUsuario) {
            throw new SecurityException("Você só pode registrar progresso nas suas próprias missões.");
        }

        int idMissao = existente.getMissao().getIdMissao();
        MissaoMensal mensal = missaoMensalDAO.buscarPorId(idMissao);
        // O DAO nunca devolve null: se a missão não é mensal, vem um objeto padrão (id diferente).
        if (mensal == null || mensal.getIdMissao() != idMissao) {
            throw new IllegalArgumentException("A missão informada não é uma missão mensal.");
        }
        LocalDate hoje = LocalDate.now();
        if (mensal.getDataInicio() == null || mensal.getDataFim() == null
                || hoje.isBefore(mensal.getDataInicio()) || hoje.isAfter(mensal.getDataFim())) {
            throw new IllegalArgumentException("A missão está fora do período de vigência.");
        }

        UsuarioMissaoDAO.Avanco avanco = usuarioMissaoDAO.avancarProgressoMensal(idUsuario, idMissao,
                mensal.getMeta(), hoje);
        if (avanco.concluiu()) {
            // Conclusão válida e já gravada: ranking geral (all time e mês) e streak do mês.
            rankingAllTimeService.registrarMissaoConcluida(idUsuario);
            streakService.registrarAtividade(idUsuario, hoje);
        }
        return buscarPorId(id);
    }

    private void validar(UsuarioMissaoRequestDTO dto, int idUsuario) {
        if (idUsuario <= 0) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.idMissao() <= 0) {
            System.out.println("ERRO: A missão é obrigatória.");
            throw new IllegalArgumentException("A missão é obrigatória.");
        }
        if (usuarioDAO.buscarPorId(idUsuario) == null) {
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