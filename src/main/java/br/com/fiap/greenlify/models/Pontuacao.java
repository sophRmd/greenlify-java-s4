package br.com.fiap.greenlify.models;

import br.com.fiap.greenlify.enums.DificuldadeEnum;
import br.com.fiap.greenlify.enums.ImpactoEnum;

/**
 * Representa a pontuação atribuída a uma missão, calculada a partir da
 * combinação entre o impacto ambiental e a dificuldade de realização.
 */
public class Pontuacao {

    private int idPontuacao;
    private ImpactoEnum impacto;
    private DificuldadeEnum dificuldade;
    private int valorBase;

    /**
     * Cria uma pontuação sem dados definidos.
     */
    public Pontuacao() {
    }

    /**
     * Calcula o valor base de pontos com base na combinação entre impacto e
     * dificuldade da missão.
     *
     * @param impacto     nível de impacto ambiental da missão
     * @param dificuldade nível de dificuldade da missão
     * @return o valor base de pontos correspondente à combinação informada,
     *         ou {@code 0} caso algum dos parâmetros seja nulo
     */
    public int calculaPontuacao(ImpactoEnum impacto, DificuldadeEnum dificuldade) {
        if (impacto == null || dificuldade == null) {
            return 0;
        }

        return switch (impacto) {
            case BAIXO -> switch (dificuldade) {
                case FACIL -> 20;
                case MEDIO -> 40;
                case DIFICIL -> 60;
            };
            case MEDIO -> switch (dificuldade) {
                case FACIL -> 40;
                case MEDIO -> 60;
                case DIFICIL -> 80;
            };
            case ALTO -> switch (dificuldade) {
                case FACIL -> 60;
                case MEDIO -> 80;
                case DIFICIL -> 100;
            };
        };
    }

    public int getIdPontuacao() {
        return idPontuacao;
    }

    public void setIdPontuacao(int idPontuacao) {
        this.idPontuacao = idPontuacao;
    }

    public ImpactoEnum getImpacto() {
        return impacto;
    }

    public void setImpacto(ImpactoEnum impacto) {
        this.impacto = impacto;
        if (this.dificuldade != null) {
            this.valorBase = calculaPontuacao(this.impacto, this.dificuldade);
        }
    }

    public DificuldadeEnum getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(DificuldadeEnum dificuldade) {
        this.dificuldade = dificuldade;
        if (this.impacto != null) {
            this.valorBase = calculaPontuacao(this.impacto, this.dificuldade);
        }
    }

    public int getValorBase() {
        return valorBase;
    }

    public void setValorBase(int valorBase) {
        this.valorBase = valorBase;
    }

    @Override
    public String toString() {
        return "ID Pontuação: " + idPontuacao +
                "\nImpacto: " + impacto +
                "\nDificuldade: " + dificuldade +
                "\nPontos: " + valorBase;
    }
}