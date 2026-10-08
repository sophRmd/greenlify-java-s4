package br.com.fiap.greenlify.models;

public class UsuarioMissao {
    private int idUsuarioMissao;
    private int tentativas;
    private int progressoAtual;
    private Usuario usuario;
    private Missao missao;

    public int getIdUsuarioMissao() {
        return idUsuarioMissao;
    }

    public void setIdUsuarioMissao(int idUsuarioMissao) {
        this.idUsuarioMissao = idUsuarioMissao;
    }

    public int getTentativas() {
        return tentativas;
    }

    public void setTentativas(int tentativas) {
        this.tentativas = tentativas;
    }

    public int getProgressoAtual() {
        return progressoAtual;
    }

    public void setProgressoAtual(int progressoAtual) {
        this.progressoAtual = progressoAtual;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Missao getMissao() {
        return missao;
    }

    public void setMissao(Missao missao) {
        this.missao = missao;
    }
}
