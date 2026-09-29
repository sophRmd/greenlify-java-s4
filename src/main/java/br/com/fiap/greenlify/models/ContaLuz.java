package br.com.fiap.greenlify.models;

import java.time.LocalDate;

/**
 * Representa uma conta de energia elétrica vinculada a um {@link UsuarioComum},
 * que pode ser quitada utilizando o saldo em reais disponível em sua
 * {@link Carteira}.
 */
public class ContaLuz {

    private int idContaLuz;
    private UsuarioComum usuario;
    private String codigoBarras;
    private double valorConta;
    private LocalDate dataPagamento;

    /**
     * Realiza o pagamento da conta de luz utilizando o saldo da carteira
     * informada. O valor da conta é debitado da carteira somente se houver
     * saldo suficiente.
     *
     * @param carteira carteira do usuário de onde o valor será debitado
     * @return {@code true} se o pagamento foi realizado com sucesso;
     *         {@code false} caso o saldo seja insuficiente
     */
    public boolean pagarConta(Carteira carteira) {
        if (carteira.getValorReal() >= valorConta) {
            carteira.setValorReal(carteira.getValorReal() - valorConta);
            setDataPagamento(LocalDate.now());
            return true;
        } else {
            return false;
        }
    }

    public int getIdContaLuz() {
        return idContaLuz;
    }

    public void setIdContaLuz(int idContaLuz) {
        this.idContaLuz = idContaLuz;
    }

    public double getValorConta() {
        return valorConta;
    }

    public void setValorConta(double valorConta) {
        this.valorConta = valorConta;
    }

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public UsuarioComum getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioComum usuario) {
        this.usuario = usuario;
    }
}