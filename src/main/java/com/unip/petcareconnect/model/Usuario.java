package com.unip.petcareconnect.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private int id;
    private String nome;
    private String login;
    private String perfil;
    private String email;
    private String telefone;
    private boolean ativo;

    public Usuario() {}

    public Usuario(int id, String nome, String login, String perfil, String email, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.perfil = perfil;
        this.email = email;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
}
