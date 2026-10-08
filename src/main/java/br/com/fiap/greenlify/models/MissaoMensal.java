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

    @Override
    public int getIdMissao() {
        return idMissao;
    }

    @Override
    public void setIdMissao(int idMissao) {
        this.idMissao = idMissao;
    }
}