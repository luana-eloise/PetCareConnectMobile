package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Item de GET /api/Relatorios:
 * {id, petId, petNome, tutorNome, texto, status "Disponivel", atualizacao "dd/MM/yyyy HH:mm"}.
 */
public class Relatorio implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_DISPONIVEL = "Disponivel";

    private int id;
    private int petId;
    private String petNome;
    private String tutorNome;
    private String texto;
    private String status;
    private String atualizacao;

    public Relatorio() {}

    public int getId() { return id; }
    public int getPetId() { return petId; }
    public String getPetNome() { return petNome != null ? petNome : ""; }
    public String getTutorNome() { return tutorNome != null ? tutorNome : ""; }
    public String getTexto() { return texto != null ? texto : ""; }
    public String getStatus() { return status != null ? status : ""; }
    public String getAtualizacao() { return atualizacao != null ? atualizacao : ""; }

    public String getTitulo() { return "Relatório Clínico — " + getPetNome(); }

    /** O valor da API é "Disponivel" (sem acento). */
    public String getStatusFormatado() {
        return STATUS_DISPONIVEL.equalsIgnoreCase(status) ? "Disponível" : getStatus();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Relatorio) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}