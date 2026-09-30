package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.GrupoDAO;
import br.com.fiap.greenlify.models.Grupo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoService {
    private final GrupoDAO grupoDAO;

    public GrupoService(){
        this.grupoDAO = new GrupoDAO();
    }

    public Grupo buscarPorId(int id){
        return grupoDAO.buscarPorId(id);
    }

    public void cadastrar(Grupo grupo){
        if(grupo.getNome() == null && grupo.getNome().isBlank()){
            System.out.println("ERRO: Nome do grupo não pode ser vazio.");
            throw new IllegalArgumentException("Nome do grupo não pode ser vazio.");
        }
        if(grupo.getLimiteUsuarios() < 1){
            System.out.println("ERRO: o grupo deve haver pelo menos um usuário.");
            throw new IllegalArgumentException("O grupo deve haver pelo menos um usuário.");
        }
        if(grupo.getStatusGrupo() == 0){
            System.out.println("ERRO: Não é possível criar grupo inativo.");
            throw new IllegalArgumentException("Não é possível criar grupo inativo.");
        }
        grupoDAO.cadastrar(grupo);
    }

    public List<Grupo> listar(){
        return grupoDAO.listar();
    }

    public void alterar(Grupo grupo){
        if(grupo.getNome() == null && grupo.getNome().isBlank()){
            System.out.println("ERRO: Nome do grupo não pode ser vazio.");
            throw new IllegalArgumentException("Nome do grupo não pode ser vazio.");
        }
        if(grupo.getLimiteUsuarios() < 1){
            System.out.println("ERRO: o grupo deve haver pelo menos um usuário.");
            throw new IllegalArgumentException("O grupo deve haver pelo menos um usuário.");
        }
        if(grupo.getStatusGrupo() == 0){
            System.out.println("ERRO: Não é possível criar grupo inativo.");
            throw new IllegalArgumentException("Não é possível criar grupo inativo.");
        }
        grupoDAO.alterar(grupo);
    }

    public void excluir(int id){
        if(buscarPorId(id) == null){
            System.out.println("ERRO: Grupo não encontrado.");
            throw new IllegalArgumentException("ID não pertence a nenhum grupo.");
        }
        grupoDAO.excluir(id);
    }
}
