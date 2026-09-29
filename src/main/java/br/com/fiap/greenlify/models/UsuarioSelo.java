package br.com.fiap.greenlify.models;

import java.time.LocalDate;

/**
 * Representa a associação entre um {@link Usuario} e um {@link Selo}
 * conquistado, registrando a data em que o selo foi coletado.
 */
public class UsuarioSelo {

    private int idUsuarioSelo;
    private LocalDate dataColeta;
    private Usuario usuario;
    private Selo selo;

    public int getIdUsuarioSelo() {
        return idUsuarioSelo;
    }

    public void setIdUsuarioSelo(int idUsuarioSelo) {
        this.idUsuarioSelo = idUsuarioSelo;
    }

    public LocalDate getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(LocalDate dataColeta) {
        this.dataColeta = dataColeta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Selo getSelo() {
        return selo;
    }

    public void setSelo(Selo selo) {
        this.selo = selo;
    }
}