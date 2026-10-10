package com.unip.petcareconnect.model;

import java.io.Serializable;

public class CadastroRequest implements Serializable {
    private String nome;
    private String login;
    private String email;
    private String senha;
    private String perfil = "Tutor";

    public CadastroRequest() {}

    public CadastroRequest(String nome, String login, String email, String senha) {
        this.nome = nome;
        this.login = login;
        this.email = email;
        this.senha = senha;
        this.perfil = "Tutor";
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}
