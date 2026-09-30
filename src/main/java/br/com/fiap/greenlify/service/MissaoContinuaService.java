package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.MissaoContinuaDAO;
import br.com.fiap.greenlify.models.MissaoContinua;
import org.springframework.stereotype.Service;

@Service
public class MissaoContinuaService {
    private final MissaoContinuaDAO missaoContinuaDAO;

    public MissaoContinuaService(){
        this.missaoContinuaDAO = new MissaoContinuaDAO();
    }

    public void cadastrar(MissaoContinua missaoContinua){
        if(missaoContinua.getNome() == null || missaoContinua.getNome().isBlank()){
            System.out.println("ERRO: Nome da missão não pode ser vazio.");
            throw new IllegalArgumentException("Nome da missão não pode ser vazio.");
        }
        if(missaoContinua.getDescricao() == null || missaoContinua.getDescricao().isBlank()){
            System.out.println("ERRO: Descrição da missão não pode ser vazia.");
            throw new IllegalArgumentException("Descrição da missão não pode ser vazia.");
        }
        if(missaoContinua.getComoComprovar() == null || missaoContinua.getComoComprovar().isBlank()){
            System.out.println("ERRO: Descrição de comprovação da missão não pode ser vazia.");
            throw new IllegalArgumentException("Descrição de comprovação da missão não pode ser vazia.");
        }
        if(missaoContinua.getRequisitos() == null || missaoContinua.getRequisitos().isBlank()){
            System.out.println("ERRO: Requisitos da missão não pode ser vazios.");
            throw new IllegalArgumentException("Requisitos da missão não pode ser vazios.");
        }
        if(missaoContinua.getCategoria() == null){
            System.out.println("ERRO: Categoria da missão não pode ser vazia.");
            throw new IllegalArgumentException("Categoria da mmissão não pode ser vazia.");
        }
        if(missaoContinua.getTipoMissao() == null){
            System.out.println("ERRO: Tipo da missão não pode ser vazio.");
            throw new IllegalArgumentException("Tipo da mmissão não pode ser vazio.");
        }
        if(missaoContinua.getLimiteRealizacoes() <= 0){
            System.out.println("ERRO: Missão deve ser possível realizar pelo menos uma vez.");
            throw new IllegalArgumentException("Missão deve ser possível realizar pelo menos uma vez.");
        }
        if(missaoContinua.getTentativasUsuario() <= 0){
            System.out.println("ERRO: Usuário deve poder realizar a missão pelo menos uma vez.");
            throw new IllegalArgumentException("Usuário deve poder realizar a missão pelo menos uma vez.");
        }
        if(missaoContinua.getValorProgresso() < 0){
            System.out.println("ERRO: Valor do progresso não pode ser negativo.");
            throw new IllegalArgumentException("Valor do progresso não pode ser negativo.");
        }
        if(missaoContinua.getTipoMetrica() == null){
            System.out.println("ERRO: Tipo de métrica da missão não pode ser vazios.");
            throw new IllegalArgumentException("Tipo de métrica da missão não pode ser vazios.");
        }
        if(missaoContinua.getValorMetrica() < 0){
            System.out.println("ERRO: Valor da métrica não pode ser negativo.");
            throw new IllegalArgumentException("Valor da métrica não pode ser negativo.");
        }
        if(missaoContinua.getIdPontuacao() <= 0){
            System.out.println("ERRO: Pontuação deve ser maior que um.");
            throw new IllegalArgumentException("Pontuação deve ser maior que um..");
        }
        missaoContinuaDAO.cadastrarMissao(missaoContinua);
    }
}
