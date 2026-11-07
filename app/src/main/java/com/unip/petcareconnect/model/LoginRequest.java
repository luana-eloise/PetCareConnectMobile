package com.unip.petcareconnect.model;

import java.io.Serializable;

/**
 * Corpo do POST /api/Auth/login: {login, senha}.
 */
public class LoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String senha;

    public LoginRequest(String login, String senha) {
        this.login = login != null ? login.trim().toLowerCase() : "";
        this.senha = senha;
    }

    public String getLogin() { return login; }
    public String getSenha() { return senha; }
}