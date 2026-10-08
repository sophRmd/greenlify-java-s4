package br.com.fiap.greenlify.models;

/**
 * Representa uma recompensa que pode ser concedida a um usuário, geralmente
 * associada a uma colocação no {@link Ranking}, concedendo um bônus
 * percentual de pontos.
 */
public class Recompensa {

    private int idRecompensa;
    private int bonusPercentual;
    private int bonusPontos;
    private String descricao;

    public int getIdRecompensa() {
        return idRecompensa;
    }

    public void setIdRecompensa(int idRecompensa) {
        this.idRecompensa = idRecompensa;
    }

    public int getBonusPercentual() {
        return bonusPercentual;
    }

    public void setBonusPercentual(int bonusPercentual) {
        this.bonusPercentual = bonusPercentual;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getBonusPontos() {
        return bonusPontos;
    }

    public void setBonusPontos(int bonusPontos) {
        this.bonusPontos = bonusPontos;
    }
}