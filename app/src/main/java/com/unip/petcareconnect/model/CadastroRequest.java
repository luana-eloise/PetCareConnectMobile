package com.unip.petcareconnect.model;

import java.io.Serializable;

/**
 * Corpo do POST /api/Auth/cadastro: {nome, login, email, senha}.
 * A API cria sempre um Tutor, então não existe campo de perfil.
 */
public class CadastroRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final String login;
    private final String email;
    private final String senha;

    public CadastroRequest(String nome, String login, String email, String senha) {
        this.nome = nome != null ? nome.trim() : "";
        this.login = login != null ? login.trim().toLowerCase() : "";
        this.email = email != null ? email.trim().toLowerCase() : "";
        this.senha = senha;
    }

    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
}