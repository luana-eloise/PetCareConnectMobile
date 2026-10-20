package com.unip.petcareconnect.model;

import java.io.Serializable;

public class LoginRequest implements Serializable {
    private String login;
    private String senha;
    private String perfil;

    public LoginRequest() {}

    public LoginRequest(String login, String senha, String perfil) {
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
}
