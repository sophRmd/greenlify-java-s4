package br.com.fiap.greenlify.service;

import br.com.fiap.greenlify.dao.AdminDAO;
import br.com.fiap.greenlify.enums.StatusContaEnum;
import br.com.fiap.greenlify.models.Admin;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AdminService {
    private final AdminDAO adminDAO;
    public AdminService(){
        this.adminDAO = new AdminDAO();
    }

    public void cadastrar (Admin admin){
        if(admin.getNivelAcesso() != 0 && admin.getNivelAcesso() != 1){
            System.out.println("ERRO: Senha não pode ser vazia.");
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        if(admin.getStatusConta() != StatusContaEnum.ATIVA){
            System.out.println("ERRO: Não é possível criar uma conta inativa.");
            throw new IllegalArgumentException("Não é possível criar uma conta inativa.");
        }
        adminDAO.cadastrar(admin);
    }

    public void alterarSenha(Admin admin){
        adminDAO.alterarSenha(admin);
    }


}
