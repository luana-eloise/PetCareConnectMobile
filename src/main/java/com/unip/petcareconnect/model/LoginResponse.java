package com.unip.petcareconnect.model;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private String token;
    private int usuarioId;
    private String nome;
    private String login;
    private String perfil;

    public LoginResponse() {}

    public LoginResponse(String token, int usuarioId, String nome, String login, String perfil) {
        this.token = token;
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.login = login;
        this.perfil = perfil;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getUserId() { return usuarioId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return login; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getRole() { return perfil; }

}
