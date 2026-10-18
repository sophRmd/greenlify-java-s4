package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dto.FingerprintFrameRequestDTO;
import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.FingerprintDAO;
import br.com.fiap.greenlify.dao.FingerprintFrameDAO;
import br.com.fiap.greenlify.dao.CarteiraDAO;
import br.com.fiap.greenlify.dao.MissaoContinuaDAO;
import br.com.fiap.greenlify.dao.UploadDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dao.UsuarioMissaoDAO;
import br.com.fiap.greenlify.dto.UploadRequestDTO;
import br.com.fiap.greenlify.dto.UploadResponseDTO;
import br.com.fiap.greenlify.enums.StatusContaEnum;
import br.com.fiap.greenlify.enums.StatusUploadEnum;
import br.com.fiap.greenlify.enums.TipoMissaoEnum;
import br.com.fiap.greenlify.mapper.ReferenciaMapper;
import br.com.fiap.greenlify.mapper.UploadMapper;
import br.com.fiap.greenlify.models.Carteira;
import br.com.fiap.greenlify.models.Fingerprint;
import br.com.fiap.greenlify.models.FingerprintFrame;
import br.com.fiap.greenlify.models.MissaoContinua;
import br.com.fiap.greenlify.models.Upload;
import br.com.fiap.greenlify.models.UsuarioComum;
import br.com.fiap.greenlify.models.UsuarioMissao;
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
    private final UsuarioMissaoDAO usuarioMissaoDAO;
    private final CarteiraDAO carteiraDAO;

    /** Confiança (%) a partir da qual o upload é reprovado por duplicidade. */
    private static final int CONFIANCA_REPROVAR = 80;
    /** Confiança (%) a partir da qual o upload fica parcialmente aprovado (suspeita). */
    private static final int CONFIANCA_PARCIAL = 50;
    private static final int MAXIMO_FRAMES = 120;
    private static final Pattern PHASH = Pattern.compile("^[0-9a-fA-F]{8,64}$");

    public UploadService() {
        this.uploadDAO = new UploadDAO();
        this.usuarioDAO = new UsuarioComumDAO();
        this.missaoDAO = new MissaoContinuaDAO();
        this.fingerprintDAO = new FingerprintDAO();
        this.frameDAO = new FingerprintFrameDAO();
        this.usuarioMissaoDAO = new UsuarioMissaoDAO();
        this.carteiraDAO = new CarteiraDAO();
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
     * duplicidade e grava Upload, Fingerprint e Frames.
     *
     * <p>A IA (Python) ainda não está integrada: hash e frames chegam na requisição.</p>
     *
     * @return o upload gravado, com o resultado da análise
     * @throws IllegalStateException se o banco não confirmar a gravação
     */
    public UploadResponseDTO cadastrar(UploadRequestDTO dto) {
        validar(dto);

        UsuarioComum usuario = usuarioDAO.buscarPorId(dto.idUsuario());
        Upload upload = new Upload();
        boolean arquivoValido = upload.verificaArquivo(
                dto.arquivo(), dto.tamanhoMB(), dto.idMissao(), usuario) != null;

        if (arquivoValido) {
            analisarDuplicidade(upload, dto.frames());
        }

        uploadDAO.cadastrar(upload);
        if (upload.getIdUpload() <= 0) {
            throw new IllegalStateException("Não foi possível gravar o upload.");
        }

        if (arquivoValido) {
            Fingerprint fingerprint = new Fingerprint();
            fingerprint.setHashVideo(dto.hashVideo());
            fingerprint.setDataGeracao(LocalDate.now());
            fingerprint.setUpload(upload);
            fingerprintDAO.cadastrar(fingerprint);

            // o DAO não devolve o id gerado; a fingerprint é única por upload
            Fingerprint gravada = buscarFingerprintDoUpload(upload.getIdUpload());
            if (gravada == null) {
                throw new IllegalStateException("Não foi possível gravar a fingerprint do upload.");
            }
            for (FingerprintFrameRequestDTO f : dto.frames()) {
                FingerprintFrame frame = new FingerprintFrame();
                frame.setNumeroFrame(f.numeroFrame());
                frame.setTempoSegundo(f.tempoSegundo());
                frame.setpHash(f.pHash());
                frame.setEmbedding(f.embedding());
                frame.setFingerprint(gravada);
                frameDAO.cadastrar(frame);
            }
        }

        registrarResultado(usuario, dto.idMissao(), upload.getStatus() == StatusUploadEnum.APROVADO);

        return UploadMapper.toDto(upload);
    }

    /**
     * Reflete o upload no progresso do usuário na missão: toda tentativa soma em
     * {@code tentativas}; só upload APROVADO soma em {@code progressoAtual}. Ao
     * atingir o limite de realizações, a missão é concluída e os pontos dela
     * vão para a carteira. (Upload parcialmente aprovado aguarda revisão e não
     * pontua.)
     */
    private void registrarResultado(UsuarioComum usuario, int idMissao, boolean aprovado) {
        int idUsuario = usuario.getIdUsuario();
        UsuarioMissao andamento = buscarUsuarioMissao(idUsuario, idMissao);
        if (andamento == null) {
            andamento = new UsuarioMissao();
            andamento.setUsuario(ReferenciaMapper.usuario(idUsuario));
            andamento.setMissao(ReferenciaMapper.missao(idMissao));
            andamento.setTentativas(1);
            andamento.setProgressoAtual(aprovado ? 1 : 0);
            usuarioMissaoDAO.cadastrar(andamento);
        } else {
            andamento.setTentativas(andamento.getTentativas() + 1);
            if (aprovado) {
                andamento.setProgressoAtual(andamento.getProgressoAtual() + 1);
            }
            usuarioMissaoDAO.alterar(andamento);
        }

        MissaoContinua missao = missaoDAO.buscarPorId(idMissao);
        if (aprovado && andamento.getProgressoAtual() == missao.getLimiteRealizacoes()) {
            creditarPontos(usuario, missao.getPontuacao().getValorBase());
        }
    }

    private void creditarPontos(UsuarioComum usuario, int pontos) {
        Carteira carteira = carteiraDAO.buscarPorIdUsuario(usuario.getIdUsuario());
        if (carteira == null) {
            // a carteira usa o id do usuário como chave (ver CarteiraDAO.cadastrar)
            carteira = new Carteira();
            carteira.setUsuario(usuario);
            carteira.setIdCarteira(usuario.getIdUsuario());
            carteiraDAO.cadastrar(carteira);
        }
        carteira.setQtdPontos(carteira.getQtdPontos() + pontos);
        carteira.recalcularValorReal();
        carteiraDAO.atualizarSaldoPontos(carteira);
    }

    private UsuarioMissao buscarUsuarioMissao(int idUsuario, int idMissao) {
        for (UsuarioMissao um : usuarioMissaoDAO.listarPorUsuario(idUsuario)) {
            if (um.getMissao() != null && um.getMissao().getIdMissao() == idMissao) {
                return um;
            }
        }
        return null;
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

    private Fingerprint buscarFingerprintDoUpload(int idUpload) {
        for (Fingerprint fp : fingerprintDAO.listar()) {
            if (fp.getUpload() != null && fp.getUpload().getIdUpload() == idUpload) {
                return fp;
            }
        }
        return null;
    }

    private void validar(UploadRequestDTO dto) {
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
        // O DAO nunca devolve null: se o id não existe, retorna um objeto com os valores padrão.
        MissaoContinua missao = missaoDAO.buscarPorId(dto.idMissao());
        if (missao.getIdMissao() != dto.idMissao() || missao.getTipoMissao() != TipoMissaoEnum.MISSAO_CONTINUA) {
            System.out.println("ERRO: Missão informada não existe.");
            throw new IllegalArgumentException("A missão informada não existe.");
        }
        UsuarioMissao andamento = buscarUsuarioMissao(dto.idUsuario(), dto.idMissao());
        if (andamento != null && andamento.getProgressoAtual() >= missao.getLimiteRealizacoes()) {
            System.out.println("ERRO: Missão já concluída pelo usuário.");
            throw new IllegalArgumentException("O usuário já concluiu essa missão.");
        }

        // Anti-injection (SQL/HTML/path traversal) e whitelist de campos estruturados
        ValidadorEntrada.nomeArquivo(dto.arquivo());
        ValidadorEntrada.hash("O hash do vídeo", dto.hashVideo());

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
    }
}
