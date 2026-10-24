package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.DadosDemoDAO;
import org.springframework.stereotype.Service;

/**
 * Restaura os dados de demonstração das fingerprints executando o script
 * {@code db/seed_demo.sql} (apaga Fingerprint_Frame e Fingerprint e recarrega o exemplo).
 * <p>
 * Não mexe no ranking (nem no Oracle, nem no Redis).
 * Como a operação é destrutiva, só roda se a variável de ambiente SEED_HABILITADO for "true".
 */
@Service
public class DadosDemoService {
    private final DadosDemoDAO dao;

    public DadosDemoService() {
        this.dao = new DadosDemoDAO();
    }

    /** Informa se a restauração está liberada neste ambiente (SEED_HABILITADO=true). */
    public boolean estaHabilitado() {
        return "true".equalsIgnoreCase(System.getenv("SEED_HABILITADO"));
    }

    /**
     * Executa o script seed_demo.sql e mais nada.
     *
     * @return mensagem com o resultado
     * @throws IllegalStateException se o banco falhar
     */
    public String restaurar() {
        int comandos = dao.restaurar();
        return "Dados de demonstração restaurados (" + comandos + " comandos executados).";
    }
}