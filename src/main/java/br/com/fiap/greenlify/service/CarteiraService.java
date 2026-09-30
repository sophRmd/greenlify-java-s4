package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.CarteiraDAO;
import br.com.fiap.greenlify.models.Carteira;
import org.springframework.stereotype.Service;

@Service
public class CarteiraService {
    private final CarteiraDAO carteiraDAO;

    public CarteiraService(){
        this.carteiraDAO = new CarteiraDAO();
    }

    public Carteira buscarPorId(int id){
        if(id < 0){
            System.out.println("ERRO: ID não pode ser menor que zero.");
            throw new IllegalArgumentException("ID não pode ser menor que zero.");
        }
        var carteira = carteiraDAO.buscarPorId(id);
        return carteira;
    }

    public void cadastrar(Carteira carteira){
        if(carteira.getQtdPontos() < 0){
            System.out.println("ERRO: Não é possível ter pontos negativos na carteira.");
            throw new IllegalArgumentException("Não é possível ter pontos negativos na carteira.");
        }
        if(carteira.getValorReal() < 0){
            System.out.println("ERRO: Não é possível ter saldo negativo na carteira.");
            throw new IllegalArgumentException("Não é possível ter saldo negativo na carteira.");
        }
        carteiraDAO.cadastrar(carteira);
    }

    public void atualizarSaldoPontos(Carteira carteira){
        if(carteira.getQtdPontos() < 0){
            System.out.println("ERRO: Não é possível ter pontos negativos na carteira.");
            throw new IllegalArgumentException("Não é possível ter pontos negativos na carteira.");
        }
        if(carteira.getValorReal() < 0){
            System.out.println("ERRO: Não é possível ter saldo negativo na carteira.");
            throw new IllegalArgumentException("Não é possível ter saldo negativo na carteira.");
        }
        carteiraDAO.atualizarSaldoPontos(carteira);
    }
}
