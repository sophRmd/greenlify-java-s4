package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dto.FingerprintFrameRequestDTO;
import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.FingerprintDAO;
import br.com.fiap.greenlify.dao.FingerprintFrameDAO;
import br.com.fiap.greenlify.dao.MissaoContinuaDAO;
import br.com.fiap.greenlify.dao.UploadDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.UploadRequestDTO;
import br.com.fiap.greenlify.dto.UploadResponseDTO;
import br.com.fiap.greenlify.enums.StatusContaEnum;
import br.com.fiap.greenlify.enums.StatusUploadEnum;
import br.com.fiap.greenlify.enums.TipoMissaoEnum;
import br.com.fiap.greenlify.mapper.UploadMapper;
import br.com.fiap.greenlify.models.Fingerprint;
import br.com.fiap.greenlify.models.FingerprintFrame;
import br.com.fiap.greenlify.models.MissaoContinua;
import br.com.fiap.greenlify.models.Upload;
import br.com.fiap.greenlify.models.UsuarioComum;
import br.com.fiap.greenlify.util.FrameComparavel;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class UploadService {
    private final UploadDAO uploadDAO;
    private final UsuarioComumDAO usuarioDAO;
    private final MissaoContinuaDAO missaoDAO;
    private final FingerprintDAO fingerprintDAO;
    private final FingerprintFrameDAO frameDAO;
    private final RankingAllTimeService rankingAllTimeService;

    /** Confiança (%) a partir da qual o upload é reprovado por duplicidade. */
    private static final int CONFIANCA_REPROVAR = 80;
    /** Confiança (%) a partir da qual o upload fica parcialmente aprovado (suspeita). */
    private static final int CONFIANCA_PARCIAL = 50;
    private static final int MAXIMO_FRAMES = 120;
    private static final Pattern PHASH = Pattern.compile("^[0-9a-fA-F]{8,64}$");

    /**
     * Travas por usuário: envios do mesmo usuário rodam um de cada vez (o mesmo usuário cai
     * sempre na mesma trava; usuários diferentes só dividem trava por coincidência do resto
     * da divisão, o que apenas os coloca em fila). A trava no banco (FOR UPDATE, em
     * {@link UploadDAO#registrarEnvio}) cobre também outras instâncias da aplicação.
     */
    private static final Object[] TRAVAS_POR_USUARIO = criarTravas(64);

    private static Object[] criarTravas(int quantidade) {
        Object[] travas = new Object[quantidade];
        for (int i = 0; i < quantidade; i++) {
            travas[i] = new Object();
        }
        return travas;
    }

    public UploadService() {
        this.uploadDAO = new UploadDAO();
        this.usuarioDAO = new UsuarioComumDAO();
        this.missaoDAO = new MissaoContinuaDAO();
        this.fingerprintDAO = new FingerprintDAO();
        this.frameDAO = new FingerprintFrameDAO();
        this.rankingAllTimeService = new RankingAllTimeService();
    }

    public UploadResponseDTO buscarPorId(int id) {
        Upload upload = uploadDAO.buscarPorId(id);
        if (upload == null) {
            throw new IllegalArgumentException("Upload não encontrado");
        }
        return UploadMapper.toDto(upload);
    }

    public List<UploadResponseDTO> listar() {
        return uploadDAO.listar()
                .stream()
                .map(UploadMapper::toDto)
                .toList();
    }

    /**
     * Registra o envio de uma comprovação: valida o arquivo (regras do model
     * {@link Upload}), compara os frames (pHash e embedding, gerados pela IA)
     * com os de todos os uploads já gravados, define status e confiança de
     * duplicidade e grava tudo em uma única transação: Upload, Fingerprint, Frames,
     * progresso na missão e, quando a conclusão é válida, o crédito dos pontos.
     *
     * <p>O progresso das missões contínuas é mensal: conta os uploads aprovados no mês
     * corrente, então o limite de realizações vale por mês e o usuário volta a pontuar
     * no mês seguinte. Missão concluída soma a pontuação elegível no ranking all time (Redis); os
     * rankings mensais só mudam por ação do administrador.</p>
     *
     * <p>A IA (Python) ainda não está integrada: hash e frames chegam na requisição.</p>
     *
     * @return o upload gravado, com o resultado da análise
     * @throws IllegalArgumentException se o envio for inválido, a missão não existir ou não for contínua,
     *                                  o vídeo (hash) já foi enviado antes ou o limite do mês já foi atingido
     * @throws IllegalStateException    se o banco falhar (nada é gravado)
     */
    public UploadResponseDTO cadastrar(UploadRequestDTO dto) {
        if (dto.idUsuario() <= 0) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        synchronized (TRAVAS_POR_USUARIO[dto.idUsuario() % TRAVAS_POR_USUARIO.length]) {
            return cadastrarSerializado(dto);
        }
    }

    private UploadResponseDTO cadastrarSerializado(UploadRequestDTO dto) {
        MissaoContinua missao = validar(dto);

        UsuarioComum usuario = usuarioDAO.buscarPorId(dto.idUsuario());
        Upload upload = new Upload();
        boolean arquivoValido = upload.verificaArquivo(
                dto.arquivo(), dto.tamanhoMB(), dto.idMissao(), usuario) != null;

        Fingerprint fingerprint = null;
        List<FingerprintFrame> frames = new ArrayList<>();
        if (arquivoValido) {
            analisarDuplicidade(upload, dto.frames());

            fingerprint = new Fingerprint();
            fingerprint.setHashVideo(dto.hashVideo().trim());
            fingerprint.setDataGeracao(LocalDate.now());
            for (FingerprintFrameRequestDTO f : dto.frames()) {
                FingerprintFrame frame = new FingerprintFrame();
                frame.setNumeroFrame(f.numeroFrame());
                frame.setTempoSegundo(f.tempoSegundo());
                frame.setpHash(f.pHash());
                frame.setEmbedding(f.embedding());
                frames.add(frame);
            }
        }

        // Tudo (upload, fingerprint, frames, progresso e crédito) na mesma transação.
        UploadDAO.ResultadoEnvio resultado = uploadDAO.registrarEnvio(upload, fingerprint, frames, missao);

        if (resultado.concluiu()) {
            // Conclusão já gravada no Oracle: soma no ranking all time os mesmos pontos creditados na
            // carteira (lidos pelo DAO da missão já travada na transação, regra única da pontuação).
            // Se o Redis falhar, a conclusão não é desfeita; a reconciliação corrige.
            LocalDate dia = upload.getDataEnvio() != null ? upload.getDataEnvio() : LocalDate.now();
            rankingAllTimeService.registrarMissaoConcluida(
                    usuario.getIdUsuario(), missao.getIdMissao(), dia, resultado.pontos());
        }

        return UploadMapper.toDto(upload);
    }

    /**
     * Compara os frames novos com os de cada upload já gravado. A confiança de
     * duplicidade é o maior percentual de frames novos que têm um frame
     * semelhante (pHash ou embedding) em um mesmo upload anterior.
     */
    private void analisarDuplicidade(Upload upload, List<FingerprintFrameRequestDTO> framesNovos) {
        List<FrameComparavel> novos = new ArrayList<>();
        for (FingerprintFrameRequestDTO f : framesNovos) {
            novos.add(FrameComparavel.de(0, f.pHash(), f.embedding()));
        }

        Map<Integer, List<FrameComparavel>> existentes = new HashMap<>();
        for (Fingerprint fp : fingerprintDAO.listar()) {
            if (fp.getUpload() == null) {
                continue;
            }
            int idUpload = fp.getUpload().getIdUpload();
            for (FingerprintFrame f : frameDAO.listarPorFingerprint(fp.getIdFingerprint())) {
                existentes.computeIfAbsent(idUpload, k -> new ArrayList<>())
                        .add(FrameComparavel.de(idUpload, f.getpHash(), f.getEmbedding()));
            }
        }

        int melhorConfianca = 0;
        int uploadMaisSimilar = 0;
        for (Map.Entry<Integer, List<FrameComparavel>> entrada : existentes.entrySet()) {
            int semelhantes = 0;
            for (FrameComparavel novo : novos) {
                for (FrameComparavel antigo : entrada.getValue()) {
                    if (novo.semelhanteA(antigo)) {
                        semelhantes++;
                        break;
                    }
                }
            }
            int confianca = (int) Math.round(100.0 * semelhantes / novos.size());
            if (confianca > melhorConfianca) {
                melhorConfianca = confianca;
                uploadMaisSimilar = entrada.getKey();
            }
        }

        upload.setConfiancaDuplicidade(melhorConfianca);
        upload.setDataAnalise(LocalDate.now());
        if (melhorConfianca >= CONFIANCA_REPROVAR) {
            upload.setStatus(StatusUploadEnum.REPROVADO);
            upload.setAnalise(String.format(
                    "Reprovado: vídeo %d%% semelhante ao upload #%d (possível duplicidade).",
                    melhorConfianca, uploadMaisSimilar));
        } else if (melhorConfianca >= CONFIANCA_PARCIAL) {
            upload.setStatus(StatusUploadEnum.PARCIALMENTE_APROVADO);
            upload.setAnalise(String.format(
                    "Parcialmente aprovado: vídeo %d%% semelhante ao upload #%d. Requer revisão.",
                    melhorConfianca, uploadMaisSimilar));
        } else {
            upload.setStatus(StatusUploadEnum.APROVADO);
            upload.setAnalise(String.format(
                    "Aprovado: nenhuma duplicidade relevante encontrada (maior semelhança: %d%%).",
                    melhorConfianca));
        }
    }

    /** Valida o envio e devolve a missão contínua (já carregada) para quem for gravá-lo. */
    private MissaoContinua validar(UploadRequestDTO dto) {
        if (dto.arquivo() == null || dto.arquivo().isBlank()) {
            System.out.println("ERRO: O arquivo é obrigatório.");
            throw new IllegalArgumentException("O arquivo é obrigatório.");
        }
        if (dto.tamanhoMB() <= 0) {
            throw new IllegalArgumentException("O tamanho do arquivo deve ser maior que 0.");
        }
        if (dto.idUsuario() <= 0) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.idMissao() <= 0) {
            System.out.println("ERRO: A missão é obrigatória.");
            throw new IllegalArgumentException("A missão é obrigatória.");
        }
        UsuarioComum solicitante = usuarioDAO.buscarPorId(dto.idUsuario());
        if (solicitante == null) {
            System.out.println("ERRO: Usuário informado não existe.");
            throw new IllegalArgumentException("O usuário informado não existe.");
        }
        if (solicitante.getStatusConta() != StatusContaEnum.ATIVA) {
            System.out.println("ERRO: A conta do usuário não está ativa.");
            throw new IllegalArgumentException("A conta do usuário não está ativa.");
        }
        // O DAO devolve null se o id não existe ou a missão não é contínua.
        MissaoContinua missao = missaoDAO.buscarPorId(dto.idMissao());
        if (missao == null || missao.getTipoMissao() != TipoMissaoEnum.MISSAO_CONTINUA) {
            System.out.println("ERRO: Missão informada não existe.");
            throw new IllegalArgumentException("A missão informada não existe.");
        }
        // O limite de realizações vale por mês: conta os uploads aprovados do mês corrente.
        // (A conferência definitiva, sem corrida entre envios simultâneos, é refeita dentro da transação.)
        if (uploadDAO.contarAprovadosNoCiclo(dto.idUsuario(), dto.idMissao(), LocalDate.now())
                >= missao.getLimiteRealizacoes()) {
            System.out.println("ERRO: Missão já concluída pelo usuário neste mês.");
            throw new IllegalArgumentException("O usuário já concluiu essa missão neste mês.");
        }

        // Anti-injection (SQL/HTML/path traversal) e whitelist de campos estruturados
        ValidadorEntrada.nomeArquivo(dto.arquivo());
        ValidadorEntrada.hash("O hash do vídeo", dto.hashVideo());
        // Mesma regra do FingerprintService: o mesmo hash não pode pertencer a outro upload.
        if (fingerprintDAO.buscarIdUploadPorHash(dto.hashVideo()) > 0) {
            System.out.println("ERRO: Hash já registrado em outro upload.");
            throw new IllegalArgumentException("Este vídeo já foi enviado antes.");
        }

        if (dto.frames() == null || dto.frames().isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um frame analisado.");
        }
        if (dto.frames().size() > MAXIMO_FRAMES) {
            throw new IllegalArgumentException("Limite de " + MAXIMO_FRAMES + " frames por upload.");
        }
        Set<Integer> numeros = new HashSet<>();
        for (FingerprintFrameRequestDTO frame : dto.frames()) {
            if (frame == null) {
                throw new IllegalArgumentException("Frame inválido.");
            }
            if (frame.numeroFrame() < 0) {
                throw new IllegalArgumentException("O número do frame não pode ser negativo.");
            }
            if (frame.tempoSegundo() < 0) {
                throw new IllegalArgumentException("O tempo em segundos não pode ser negativo.");
            }
            if (!numeros.add(frame.numeroFrame())) {
                throw new IllegalArgumentException("Número de frame repetido: " + frame.numeroFrame() + ".");
            }
            ValidadorEntrada.hash("O pHash", frame.pHash());
            if (!PHASH.matcher(frame.pHash().trim()).matches()) {
                throw new IllegalArgumentException("O pHash deve ser hexadecimal (8 a 64 caracteres).");
            }
            ValidadorEntrada.embedding(frame.embedding());
        }
        return missao;
    }
}