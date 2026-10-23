package com.unip.petcareconnect.model;

import java.io.Serializable;

/**
 * DTO para cadastro de novo Tutor pelo aplicativo mobile.
 */
public class CadastroRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PERFIL_PADRAO = "Tutor";

    private String nome;
    private String login;
    private String email;
    private String senha;
    private String telefone;
    private String perfil = PERFIL_PADRAO;

    public CadastroRequest() {
        this.perfil = PERFIL_PADRAO;
    }

    public CadastroRequest(String nome, String login, String email, String senha) {
        this(nome, login, email, senha, null);
    }

    public CadastroRequest(String nome, String login, String email, String senha, String telefone) {
        this.nome = nome != null ? nome.trim() : "";
        this.login = login != null ? login.trim().toLowerCase() : "";
        this.email = email != null ? email.trim().toLowerCase() : "";
        this.senha = senha;
        this.telefone = telefone != null ? telefone.trim() : null;
        this.perfil = PERFIL_PADRAO;
    }

    public String getNome() {
        return nome != null ? nome : "";
    }

    public void setNome(String nome) {
        this.nome = nome != null ? nome.trim() : "";
    }

    public String getLogin() {
        return login != null ? login : "";
    }

    public void setLogin(String login) {
        this.login = login != null ? login.trim().toLowerCase() : "";
    }

    public String getEmail() {
        return email != null ? email : "";
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim().toLowerCase() : "";
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone != null ? telefone.trim() : null;
    }

    public String getPerfil() {
        return perfil != null ? perfil : PERFIL_PADRAO;
    }

    public void setPerfil(String perfil) {
        this.perfil = PERFIL_PADRAO;
    }
}
