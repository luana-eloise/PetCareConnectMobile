package com.unip.petcareconnect.model;

import java.io.Serializable;

/**
 * Campo "dados" do login: token, usuarioId, nome, login, perfil.
 */
public class LoginResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private int usuarioId;
    private String nome;
    private String login;
    private String perfil;

    public LoginResponse() {}

    public String getToken() { return token; }
    public int getUsuarioId() { return usuarioId; }
    public String getNome() { return nome != null ? nome : ""; }
    public String getLogin() { return login != null ? login : ""; }

    /** Pode ser null se a API não enviar. Quem chama deve tratar. */
    public String getPerfil() { return perfil; }
}