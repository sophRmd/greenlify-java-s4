package br.com.fiap.greenlify.models;

import java.time.LocalDate;

/**
 * Representa uma missão do tipo mensal, ou seja, uma missão com período de
 * vigência definido (data de início e fim) e uma meta de progresso a ser
 * atingida dentro desse período.
 */
public class MissaoMensal extends Missao {

    private int idMissao;
    private int meta;
    private int progressoAtual;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Override
    public String toString() {
        return "\n\nID: " + getIdMissao() +
                "\nNome: " + getNome() +
                "\nDescrição: " + getDescricao() +
                "\nComo comprovar: " + getComoComprovar() +
                "\nRequisitos: " + getRequisitos() +
                "\nCategoria: " + getCategoria() +
                "\nData de início: " + getDataInicio() +
                "\nData de término: " + getDataFim();
    }

    /**
     * Verifica se o período de vigência da missão é válido, ou seja, se a
     * data de início é anterior ou igual à data de término.
     *
     * @return {@code true} se o período for válido; {@code false} caso contrário
     */
    public boolean verificaValidade() {
        if (dataInicio == null || dataFim == null) {
            return false;
        }
        return !dataInicio.isAfter(dataFim);
    }

    /**
     * Registra o progresso do usuário na missão mensal, somando o valor
     * realizado ao progresso atual. O progresso nunca ultrapassa a meta
     * definida para a missão.
     *
     * <p>Esse valor pode representar tanto uma quantidade de missões
     * concluídas quanto uma quantidade de recurso economizado (ex.: kg de
     * CO2), dependendo de como a missão foi cadastrada — a lógica de
     * acúmulo é a mesma para os dois casos.</p>
     *
     * @param valorRealizado quantidade a ser somada ao progresso atual
     */
    public void registrarProgresso(int valorRealizado) {
        int novoProgresso = progressoAtual + valorRealizado;

        if (novoProgresso > meta) {
            novoProgresso = meta;
        }
        if (novoProgresso < 0) {
            novoProgresso = 0;
        }

        this.progressoAtual = novoProgresso;
    }

    /**
     * Verifica se a missão mensal já foi concluída, ou seja, se o
     * progresso atual atingiu ou ultrapassou a meta estabelecida.
     *
     * @return {@code true} se a missão foi concluída; {@code false} caso contrário
     */
    public boolean isConcluida() {
        return progressoAtual >= meta;
    }

    /**
     * Verifica se a data atual está dentro do período de vigência da
     * missão (entre a data de início e a data de término).
     *
     * @return {@code true} se hoje estiver dentro do período; {@code false} caso contrário
     */

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public int getMeta() {
        return meta;
    }

    public void setMeta(int meta) {
        this.meta = meta;
    }

    public int getProgressoAtual() {
        return progressoAtual;
    }

    public void setProgressoAtual(int progressoAtual) {
        this.progressoAtual = progressoAtual;
    }

    @Override
    public int getIdMissao() {
        return idMissao;
    }

    @Override
    public void setIdMissao(int idMissao) {
        this.idMissao = idMissao;
    }
}