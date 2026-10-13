package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.MissaoContinuaDAO;
import br.com.fiap.greenlify.dao.UploadDAO;
import br.com.fiap.greenlify.dao.UsuarioComumDAO;
import br.com.fiap.greenlify.dto.UploadRequestDTO;
import br.com.fiap.greenlify.dto.UploadResponseDTO;
import br.com.fiap.greenlify.mapper.UploadMapper;
import br.com.fiap.greenlify.models.Upload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UploadService {
    private final UploadDAO uploadDAO;
    private final UsuarioComumDAO usuarioDAO;
    private final MissaoContinuaDAO missaoDAO;

    public UploadService() {
        this.uploadDAO = new UploadDAO();
        this.usuarioDAO = new UsuarioComumDAO();
        this.missaoDAO = new MissaoContinuaDAO();
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

    public void cadastrar(UploadRequestDTO dto) {
        validar(dto);
        uploadDAO.cadastrar(UploadMapper.dtoToEntity(dto));
    }

    private void validar(UploadRequestDTO dto) {
        if (dto.status() == null) {
            System.out.println("ERRO: O status é obrigatório.");
            throw new IllegalArgumentException("O status é obrigatório.");
        }
        if (dto.dataEnvio() == null) {
            System.out.println("ERRO: A data de envio é obrigatória.");
            throw new IllegalArgumentException("A data de envio é obrigatória.");
        }
        if (dto.dataAnalise() != null && dto.dataAnalise().isBefore(dto.dataEnvio())) {
            System.out.println("ERRO: A data de análise não pode ser anterior à data de envio.");
            throw new IllegalArgumentException(
                    "A data de análise não pode ser anterior à data de envio.");
        }
        if (dto.arquivo() == null || dto.arquivo().isBlank()) {
            System.out.println("ERRO: O arquivo é obrigatório.");
            throw new IllegalArgumentException("O arquivo é obrigatório.");
        }
        if (dto.confiancaDuplicidade() < 0 || dto.confiancaDuplicidade() > 100) {
            System.out.println("ERRO: A confiança de duplicidade deve estar entre 0 e 100.");
            throw new IllegalArgumentException("A confiança de duplicidade deve estar entre 0 e 100.");
        }
        if (dto.usuario() == null) {
            System.out.println("ERRO: O usuário é obrigatório.");
            throw new IllegalArgumentException("O usuário é obrigatório.");
        }
        if (dto.missao() == null) {
            System.out.println("ERRO: A missão é obrigatória.");
            throw new IllegalArgumentException("A missão é obrigatória.");
        }
        if (usuarioDAO.buscarPorId(dto.usuario().getIdUsuario()) == null) {
            System.out.println("ERRO: Usuário informado não existe.");
            throw new IllegalArgumentException("O usuário informado não existe.");
        }
        if (missaoDAO.buscarPorId(dto.missao().getIdMissao()) == null) {
            System.out.println("ERRO: Missão informada não existe.");
            throw new IllegalArgumentException("A missão informada não existe.");
        }
    }
}