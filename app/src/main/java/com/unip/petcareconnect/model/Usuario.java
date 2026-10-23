package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa o Tutor autenticado no aplicativo PetCare Connect Mobile.
 * Centraliza os dados de identificação, contato e perfil exclusivo de Tutor.
 */
@SuppressWarnings("unused")
public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String PERFIL_PADRAO = "Tutor";

    private int id;
    private String nome;
    private String login;
    private String email;
    private String telefone;
    private String perfil;
    private boolean ativo;

    public Usuario() {
        this.perfil = PERFIL_PADRAO;
        this.ativo = true;
    }

    public Usuario(int id, String nome, String login, String email, boolean ativo) {
        this(id, nome, login, email, null, ativo);
    }

    public Usuario(int id, String nome, String login, String email, String telefone, boolean ativo) {
        this.id = id;
        this.nome = nome != null ? nome.trim() : "";
        this.login = login != null ? login.trim().toLowerCase() : "";
        this.email = email != null ? email.trim().toLowerCase() : null;
        this.telefone = telefone != null ? telefone.trim() : null;
        this.perfil = PERFIL_PADRAO;
        this.ativo = ativo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome != null ? nome : ""; }
    public void setNome(String nome) { this.nome = nome != null ? nome.trim() : ""; }

    public String getLogin() { return login != null ? login : ""; }
    public void setLogin(String login) { this.login = login != null ? login.trim().toLowerCase() : ""; }

    public String getPerfil() { return perfil != null ? perfil : PERFIL_PADRAO; }
    public void setPerfil(String perfil) { this.perfil = PERFIL_PADRAO; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email != null ? email.trim().toLowerCase() : null; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone != null ? telefone.trim() : null; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean isTutor() { return true; }

    public String getPrimeiroNome() {
        if (nome == null || nome.trim().isEmpty()) return "";
        String[] partes = nome.trim().split("\\s+");
        return partes[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
