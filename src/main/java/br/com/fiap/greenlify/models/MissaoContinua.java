package br.com.fiap.greenlify.models;

import br.com.fiap.greenlify.dao.PontuacaoDAO;
import br.com.fiap.greenlify.enums.CategoriaEnum;
import br.com.fiap.greenlify.enums.TipoMetricaEnum;
import br.com.fiap.greenlify.enums.TipoMissaoEnum;

/**
 * Representa uma missão do tipo contínua, ou seja, uma missão que pode ser
 * realizada repetidamente pelo usuário até atingir um limite de realizações,
 * gerando progresso mensurável (ex.: economia de água, energia ou carbono).
 */
public class MissaoContinua extends Missao {

    private int limiteRealizacoes;
    private int tentativasUsuario;
    private double valorProgresso;
    private TipoMetricaEnum tipoMetrica;
    private int valorMetrica;
    private int idPontuacao;

    /**
     * Cria uma missão contínua vazia, já definindo o tipo da missão como
     * {@link TipoMissaoEnum#MISSAO_CONTINUA}.
     */
    public MissaoContinua() {
        super();
        setTipoMissao(TipoMissaoEnum.MISSAO_CONTINUA);
    }

    /**
     * Cria uma missão contínua com todos os dados informados.
     *
     * @param idMissao         identificador da missão
     * @param nome             nome da missão
     * @param descricao        descrição da missão
     * @param comoComprovar    instruções de como comprovar a realização da missão
     * @param requisitos       requisitos necessários para realizar a missão
     * @param categoria        categoria da missão
     * @param limiteRealizacoes número máximo de vezes que a missão pode ser realizada
     * @param idPontuacao      identificador da pontuação associada à missão
     * @param valorProgresso   valor de progresso gerado a cada realização
     * @param tipoMetrica      tipo de métrica utilizada para medir o progresso
     * @param valorMetrica     valor de referência da métrica
     */
    public MissaoContinua(int idMissao, String nome, String descricao, String comoComprovar, String requisitos,
                          CategoriaEnum categoria, int limiteRealizacoes, int idPontuacao, double valorProgresso,
                          TipoMetricaEnum tipoMetrica, int valorMetrica) {

        setIdMissao(idMissao);
        setNome(nome);
        setDescricao(descricao);
        setComoComprovar(comoComprovar);
        setRequisitos(requisitos);
        setCategoria(categoria);
        setTipoMissao(TipoMissaoEnum.MISSAO_CONTINUA);

        this.limiteRealizacoes = limiteRealizacoes;
        this.idPontuacao = idPontuacao;
        this.valorProgresso = valorProgresso;
        this.tipoMetrica = tipoMetrica;
        this.valorMetrica = valorMetrica;
        this.tentativasUsuario = 0;
    }

    @Override
    public String toString() {
        PontuacaoDAO pontuacaoDAO = new PontuacaoDAO();
        Pontuacao pont = pontuacaoDAO.buscarPorId(idPontuacao);

        if (pont != null) {
            return "\n\nID: " + getIdMissao() +
                    "\nNome: " + getNome() +
                    "\nDescrição: " + getDescricao() +
                    "\nComo comprovar: " + getComoComprovar() +
                    "\nRequisitos: " + getRequisitos() +
                    "\nCategoria : " + getCategoria() +
                    "\nLimite de Realizações: " + getLimiteRealizacoes() +
                    "\nTipo de métrica: " + getTipoMetrica() +
                    "\nValor métrica: " + getValorMetrica() +
                    "\nDificuldade: " + pont.getDificuldade() +
                    "\nImpacto: " + pont.getImpacto() +
                    "\nPontos: " + pont.getValorBase();
        } else {
            return "\n\nID: " + getIdMissao() +
                    "\nNome: " + getNome() +
                    "\nDescrição: " + getDescricao() +
                    "\nComo comprovar: " + getComoComprovar() +
                    "\nRequisitos: " + getRequisitos() +
                    "\nCategoria : " + getCategoria() +
                    "\nLimite de Realizações: " + getLimiteRealizacoes() +
                    "\nTipo de métrica: " + getTipoMetrica() +
                    "\nValor métrica: " + getValorMetrica() +
                    "\nPontuação: não definida";
        }
    }

    /**
     * Atualiza o total acumulado no {@link PerfilSustentavel} do usuário de
     * acordo com o tipo de métrica da missão e retorna uma mensagem
     * descrevendo o impacto gerado.
     *
     * @param perfil perfil sustentável do usuário a ser atualizado
     * @return mensagem descrevendo o impacto ambiental gerado pela missão,
     *         ou string vazia caso o tipo de métrica não esteja definido
     */
    public String comparaImpacto(PerfilSustentavel perfil) {
        if (getTipoMetrica() == null) {
            return "";
        }

        switch (getTipoMetrica()) {
            case KG_CO2 -> {
                perfil.setTotalCarbonoEconomizado(perfil.getTotalCarbonoEconomizado() + valorProgresso);
                return "Você economizou " + valorProgresso + " " + tipoMetrica + " equivalente!";
            }
            case KWH -> {
                perfil.setTotalEnergiaEconomizada(perfil.getTotalEnergiaEconomizada() + valorProgresso);
                return "Você economizou " + valorProgresso + " " + tipoMetrica + " de energia!";
            }
            case M3 -> {
                perfil.setTotalAguaEconomizada(perfil.getTotalAguaEconomizada() + valorProgresso);
                return "Você economizou " + valorProgresso + " " + tipoMetrica + " de água!";
            }
            case KM -> {
                return "Você andou " + valorProgresso + " " + tipoMetrica + " de bicicleta!";
            }
            case ITENS_RECICLADOS -> {
                return "Você descartou " + valorProgresso + " de " + tipoMetrica + "!";
            }
            default -> {
                return "";
            }
        }
    }

    public int getLimiteRealizacoes() {
        return limiteRealizacoes;
    }

    public void setLimiteRealizacoes(int limiteRealizacoes) {
        this.limiteRealizacoes = limiteRealizacoes;
    }

    public int getTentativasUsuario() {
        return tentativasUsuario;
    }

    public void setTentativasUsuario(int tentativasUsuario) {
        this.tentativasUsuario = tentativasUsuario;
    }

    public double getValorProgresso() {
        return valorProgresso;
    }

    public void setValorProgresso(double valorProgresso) {
        this.valorProgresso = valorProgresso;
    }

    public TipoMetricaEnum getTipoMetrica() {
        return tipoMetrica;
    }

    public void setTipoMetrica(TipoMetricaEnum tipoMetrica) {
        this.tipoMetrica = tipoMetrica;
    }

    public int getValorMetrica() {
        return valorMetrica;
    }

    public void setValorMetrica(int valorMetrica) {
        this.valorMetrica = valorMetrica;
    }

    public int getIdPontuacao() {
        return idPontuacao;
    }

    public void setIdPontuacao(int idPontuacao) {
        this.idPontuacao = idPontuacao;
    }
}