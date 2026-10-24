package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.DadosDemoDAO;
import br.com.fiap.greenlify.dao.RankingRedisDAO;
import org.springframework.stereotype.Service;

/**
 * Restaura os dados de demonstração do banco (apaga e recarrega o conjunto de exemplo).
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
     * Executa o script e, depois, acerta o ranking all time do Redis com os novos totais do Oracle.
     * Se o Redis estiver fora do ar, a restauração continua valendo (o ranking all time cai para o Oracle).
     *
     * @return mensagem com o resultado
     * @throws IllegalStateException se o banco falhar
     */
    public String restaurar() {
        int comandos = dao.restaurar();
        String mensagem = "Dados de demonstração restaurados (" + comandos + " comandos executados).";

        try {
            RankingRedisDAO.Reconciliacao r = new RankingAllTimeService().reconciliar();
            if (r == null) {
                mensagem += " Redis indisponível: o ranking all time usa o Oracle.";
            } else {
                mensagem += " Ranking all time reconciliado (corrigidos: " + r.corrigidos()
                        + ", removidos: " + r.removidos() + ").";
            }
        } catch (RuntimeException e) {
            System.err.println("Não foi possível reconciliar o ranking all time: " + e);
            mensagem += " Não foi possível reconciliar o ranking all time; chame POST /rankings/all-time/reconciliar.";
        }
        return mensagem;
    }
}