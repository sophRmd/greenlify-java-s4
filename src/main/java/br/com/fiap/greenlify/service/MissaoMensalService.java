package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.MissaoMensalDAO;
import br.com.fiap.greenlify.dto.MissaoMensalCompletaRequestDTO;
import br.com.fiap.greenlify.dto.MissaoMensalCompletaResponseDTO;
import br.com.fiap.greenlify.enums.TipoMissaoEnum;
import br.com.fiap.greenlify.mapper.MissaoMensalCompletaMapper;
import br.com.fiap.greenlify.models.MissaoMensal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MissaoMensalService {

    private final MissaoMensalDAO missaoMensalDAO;

    public MissaoMensalService() {
        this.missaoMensalDAO = new MissaoMensalDAO();
    }

    public List<MissaoMensalCompletaResponseDTO> listar() {
        return missaoMensalDAO.listar()
                .stream()
                .map(MissaoMensalCompletaMapper::toDto)
                .toList();
    }

    public MissaoMensalCompletaResponseDTO buscarPorId(int id) {
        return MissaoMensalCompletaMapper.toDto(buscarExistente(id));
    }

    public void cadastrar(MissaoMensalCompletaRequestDTO dto) {
        validar(dto);

        MissaoMensal missao = MissaoMensalCompletaMapper.dtoToEntity(dto);
        // O mapper não preenche o tipo, e o DAO grava missao.getTipoMissao().toString().
        missao.setTipoMissao(TipoMissaoEnum.MISSAO_MENSAL);

        // MissaoMensal é usada como Missao e como MissaoMensal.
        missaoMensalDAO.cadastrarMissao(missao, missao);
    }

    public void atualizar(int id, MissaoMensalCompletaRequestDTO dto) {
        buscarExistente(id);
        validar(dto);

        // O DTO não carrega o id, então ele vem da URL.
        MissaoMensal missao = MissaoMensalCompletaMapper.dtoToEntity(dto);
        missao.setIdMissao(id);
        missao.setTipoMissao(TipoMissaoEnum.MISSAO_MENSAL);

        missaoMensalDAO.alterar(missao, missao);
    }

    public void excluir(int id) {
        MissaoMensal existente = buscarExistente(id);
        missaoMensalDAO.excluir(existente);
    }

    // O DAO nunca devolve null: se o id não existe, retorna um objeto com os valores padrão.
    // Também confere o tipo, para não alterar/excluir outros tipos de missão por este endpoint.
    private MissaoMensal buscarExistente(int id) {
        MissaoMensal missao = missaoMensalDAO.buscarPorId(id);
        if (missao == null
                || !Objects.equals(missao.getIdMissao(), id)
                || missao.getTipoMissao() != TipoMissaoEnum.MISSAO_MENSAL)
            throw new IllegalArgumentException("Missão mensal não encontrada");
        return missao;
    }

    private void validar(MissaoMensalCompletaRequestDTO dto) {
        if (dto.nome() == null || dto.nome().isBlank()) {
            System.out.println("ERRO: O nome da missão é obrigatório");
            throw new IllegalArgumentException("O nome da missão é obrigatório.");
        }
        if (dto.categoria() == null) {
            System.out.println("ERRO: A categoria é obrigatória");
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (dto.meta() <= 0) {
            System.out.println("ERRO: A meta deve ser maior que zero.");
            throw new IllegalArgumentException("A meta deve ser maior que zero.");
        }
        // O DAO ignora silenciosamente o cadastro/alteração sem as datas.
        if (dto.dataInicio() == null || dto.dataFim() == null) {
            System.out.println("ERRO: As datas de início e fim são obrigatórias");
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
        }
        if (dto.dataFim().isBefore(dto.dataInicio())) {
            System.out.println("ERRO: A data de fim não pode ser anterior à data de início");
            throw new IllegalArgumentException("A data de fim não pode ser anterior à data de início.");
        }

        // Anti-injection (SQL/HTML/path traversal) e whitelist de campos estruturados
        ValidadorEntrada.textoLivre("O nome da missão", dto.nome(), 200);
        ValidadorEntrada.textoLivreOpcional("A descrição", dto.descricao(), 255);
        ValidadorEntrada.textoLivreOpcional("Como comprovar", dto.comoComprovar(), 255);
        ValidadorEntrada.textoLivreOpcional("Os requisitos", dto.requisitos(), 255);
    }
}