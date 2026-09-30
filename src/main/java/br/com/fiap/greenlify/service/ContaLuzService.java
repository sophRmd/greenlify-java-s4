package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.ContaLuzDAO;
import br.com.fiap.greenlify.models.Carteira;
import br.com.fiap.greenlify.models.ContaLuz;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ContaLuzService {
    private final ContaLuzDAO contaLuzDAO;
    private final Carteira carteira;

    public ContaLuzService(){
        this.contaLuzDAO = new ContaLuzDAO();
        this.carteira = new Carteira();
    }

    public void cadastrar(ContaLuz conta){
        if(conta.getCodigoBarras() == null || conta.getCodigoBarras().isBlank()){
            System.out.println("ERRO: Código de barras não pode ser vazio.");
            throw new IllegalArgumentException("O código de barras não pode ser vazio.");
        }
        if(conta.getValorConta() <= 0){
            System.out.println("ERRO: A conta deve possuir um valor positivo.");
            throw new IllegalArgumentException("A conta a ser paga deve possuir um valor positivo.");
        }
        if(conta.getValorConta() > carteira.getValorReal()){
            System.out.println("ERRO: Valor da conta maior que o saldo.");
            throw new IllegalArgumentException("Não é possível pagar conta de valor maior que o de saldo.");
        }
        contaLuzDAO.cadastrar(conta);

    }

    public List<ContaLuz> listar(){
        return contaLuzDAO.listar();
    }

    public List<ContaLuz> listarPorUsuario(int id) {
        var contasLuz = listar();
        List<ContaLuz> contas = new ArrayList<>();
        for (ContaLuz c : contasLuz) {
            if (c.getUsuario() != null && c.getUsuario().getIdUsuario() == id) {
                contas.add(c);
            }
        }
        return contas;
    }


}
