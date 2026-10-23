package com.unip.petcareconnect.model;

import java.io.Serializable;

/**
 * DTO para autenticação de tutores no aplicativo mobile.
 */
@SuppressWarnings("unused")
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PERFIL_PADRAO = "Tutor";

    private String login;
    private String senha;
    private String perfil;

    public LoginRequest() {
        this.perfil = PERFIL_PADRAO;
    }

    public LoginRequest(String login, String senha) {
        this.login = login != null ? login.trim().toLowerCase() : "";
        this.senha = senha;
        this.perfil = PERFIL_PADRAO;
    }

    public LoginRequest(String login, String senha, String perfil) {
        this.login = login != null ? login.trim().toLowerCase() : "";
        this.senha = senha;
        this.perfil = PERFIL_PADRAO;
    }

    public String getLogin() { return login != null ? login : ""; }
    public void setLogin(String login) { this.login = login != null ? login.trim().toLowerCase() : ""; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getPerfil() { return perfil != null ? perfil : PERFIL_PADRAO; }
    public void setPerfil(String perfil) { this.perfil = PERFIL_PADRAO; }
}
