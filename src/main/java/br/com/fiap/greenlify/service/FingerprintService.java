package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.util.ValidadorEntrada;
import br.com.fiap.greenlify.dao.FingerprintDAO;
import br.com.fiap.greenlify.dao.UploadDAO;
import br.com.fiap.greenlify.dto.FingerprintRequestDTO;
import br.com.fiap.greenlify.dto.FingerprintResponseDTO;
import br.com.fiap.greenlify.mapper.FingerprintMapper;
import br.com.fiap.greenlify.models.Fingerprint;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FingerprintService {
    private final FingerprintDAO fingerprintDAO;
    private final UploadDAO uploadDAO;

    public FingerprintService() {
        this.fingerprintDAO = new FingerprintDAO();
        this.uploadDAO = new UploadDAO();
    }

    public FingerprintResponseDTO buscarPorId(int id) {
        Fingerprint fingerprint = fingerprintDAO.buscarPorId(id);
        if (fingerprint == null) {
            throw new IllegalArgumentException("Fingerprint não encontrada");
        }
        return FingerprintMapper.toDto(fingerprint);
    }

    public List<FingerprintResponseDTO> listar() {
        return fingerprintDAO.listar()
                .stream()
                .map(FingerprintMapper::toDto)
                .toList();
    }

    public void cadastrar(FingerprintRequestDTO dto) {
        validar(dto);
        fingerprintDAO.cadastrar(FingerprintMapper.dtoToEntity(dto));
    }

    public void excluir(int id) {
        Fingerprint fingerprint = fingerprintDAO.buscarPorId(id);
        if (fingerprint == null) {
            throw new IllegalArgumentException("Fingerprint não encontrada");
        }
        fingerprintDAO.excluir(id);
    }

    public void alterar(FingerprintRequestDTO dto, int id) {
        Fingerprint existente = fingerprintDAO.buscarPorId(id);
        if (existente == null) {
            throw new IllegalArgumentException("Fingerprint não encontrada");
        }
        validar(dto);

        Fingerprint fingerprint = FingerprintMapper.dtoToEntity(dto);
        fingerprint.setIdFingerprint(id);
        fingerprintDAO.alterar(fingerprint);
    }

    private void validar(FingerprintRequestDTO dto) {
        if (dto.hashVideo() == null || dto.hashVideo().isBlank()) {
            System.out.println("ERRO: O hash do vídeo é obrigatório.");
            throw new IllegalArgumentException("O hash do vídeo é obrigatório.");
        }
        if (dto.dataGeracao() == null) {
            System.out.println("ERRO: A data de geração é obrigatória.");
            throw new IllegalArgumentException("A data de geração é obrigatória.");
        }
        if (dto.dataGeracao().isAfter(LocalDate.now())) {
            System.out.println("ERRO: A data de geração não pode ser futura.");
            throw new IllegalArgumentException("A data de geração não pode ser futura.");
        }
        if (dto.idUpload() <= 0) {
            System.out.println("ERRO: O upload é obrigatório.");
            throw new IllegalArgumentException("O upload é obrigatório.");
        }
        if (uploadDAO.buscarPorId(dto.idUpload()) == null) {
            System.out.println("ERRO: Upload informado não existe.");
            throw new IllegalArgumentException("O upload informado não existe.");
        }

        // Anti-injection (SQL/HTML/path traversal) e whitelist de campos estruturados
        ValidadorEntrada.hash("O hash do vídeo", dto.hashVideo());
    }
}