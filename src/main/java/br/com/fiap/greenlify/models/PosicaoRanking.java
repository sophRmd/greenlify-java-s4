package br.com.fiap.greenlify.models;

import br.com.fiap.greenlify.dao.CarteiraDAO;
import br.com.fiap.greenlify.dao.RecompensaDAO;
import br.com.fiap.greenlify.dao.ResgateDAO;

import java.time.LocalDate;
import java.util.List;

/**
 * Representa a posição de um usuário ou grupo em um {@link Ranking},
 * incluindo sua pontuação e o processo de concessão de recompensas com base
 * na colocação obtida.
 */
public class PosicaoRanking {

    private int idPosicao;
    private int posicao;
    private int pontuacao;
    private UsuarioComum usuario;
    private Ranking ranking;
    private Grupo grupo;

    /**
     * Concede ao usuário informado a recompensa correspondente à colocação
     * desta posição no ranking, adicionando o bônus de pontos à carteira do
     * usuário e registrando o resgate.
     * <p>
     * Funciona tanto para uma posição do ranking de usuários quanto para
     * uma posição do ranking de grupos — em ambos os casos, quem recebe os
     * pontos é o usuário informado em {@code usuarioResgatador}, já que uma
     * posição de ranking de grupo não está associada a um único usuário.
     * <p>
     * A recompensa concedida varia de acordo com a posição: 1º, 2º e 3º
     * lugares recebem recompensas exclusivas, do 4º ao 10º lugar recebem uma
     * recompensa intermediária, do 11º ao 20º lugar recebem uma recompensa
     * menor, e as demais posições recebem a recompensa padrão.
     *
     * @param usuarioResgatador usuário que deve receber a recompensa
     */
    public void concederRecompensa(UsuarioComum usuarioResgatador) {

        if (usuarioResgatador == null) {
            System.out.println("Erro ao conceder recompensa.");
            return;
        }

        int idRecompensa = 6;
        if (this.posicao == 1) {
            idRecompensa = 1;
        } else if (this.posicao == 2) {
            idRecompensa = 2;
        } else if (this.posicao == 3) {
            idRecompensa = 3;
        } else if (this.posicao <= 10) {
            idRecompensa = 4;
        } else if (this.posicao <= 20) {
            idRecompensa = 5;
        }

        RecompensaDAO recompensaDAO = new RecompensaDAO();
        Recompensa recompensa = recompensaDAO.buscarPorId(idRecompensa);

        CarteiraDAO carteiraDAO = new CarteiraDAO();
        Carteira carteira = carteiraDAO.buscarPorIdUsuario(usuarioResgatador.getIdUsuario());

        if (recompensa == null) {
            System.out.println("Recompensa não encontrada.");
            return;
        }

        if (carteira == null) {
            System.out.println("Carteira não encontrada.");
            return;
        }

        int pontosAtuais = carteira.getQtdPontos();
        int percentual = recompensa.getBonusPercentual();
        int pontosAdicionais = (pontosAtuais * percentual) / 100;

        int novoSaldo = pontosAtuais + pontosAdicionais;
        carteira.setQtdPontos(novoSaldo);
        carteira.setValorReal(novoSaldo * 0.009052);

        carteiraDAO.atualizarSaldoPontos(carteira);

        Resgate resgate = new Resgate();
        resgate.setDataConcessao(LocalDate.now());
        resgate.setUsuario(usuarioResgatador);
        resgate.setRecompensa(recompensa);

        ResgateDAO resgateDAO = new ResgateDAO();
        resgateDAO.cadastrar(resgate);

        System.out.println("Recompensa concedida: " + recompensa.getDescricao());
        System.out.println("Pontos adicionados: " + pontosAdicionais);
        System.out.println("Novo saldo: " + novoSaldo);
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public UsuarioComum getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioComum usuario) {
        this.usuario = usuario;
    }

    public int getIdPosicao() {
        return idPosicao;
    }

    public void setIdPosicao(int idPosicao) {
        this.idPosicao = idPosicao;
    }

    public Ranking getRanking() {
        return ranking;
    }

    public void setRanking(Ranking ranking) {
        this.ranking = ranking;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }
}