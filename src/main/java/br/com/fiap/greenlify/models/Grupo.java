package br.com.fiap.greenlify.models;

import java.time.LocalDate;

/**
 * Representa um grupo de usuários dentro da plataforma GreenLify, utilizado
 * para agrupar {@link UsuarioComum} e compor o ranking por grupos.
 */
public class Grupo {

    private int idGrupo;
    private String nome;
    private LocalDate dataCriacao;
    private int limiteUsuarios;
    private int statusGrupo;

    @Override
    public String toString() {
        return "\n\nID: " + idGrupo +
                "\nNome: " + nome +
                "\nData de Criacao :" + dataCriacao +
                "\nLimite de usuarios: " + limiteUsuarios +
                "\nStatus do grupo: " + statusGrupo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getLimiteUsuarios() {
        return limiteUsuarios;
    }

    public void setLimiteUsuarios(int limiteUsuarios) {
        this.limiteUsuarios = limiteUsuarios;
    }

    public int getStatusGrupo() {
        return statusGrupo;
    }

    public void setStatusGrupo(int statusGrupo) {
        this.statusGrupo = statusGrupo;
    }
}