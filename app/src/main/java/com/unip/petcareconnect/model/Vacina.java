package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Item de GET /api/Vacinas/{petId}:
 * {id, petId, petNome, nome, dataAplicacao "dd/MM/yyyy", observacao, aplicada}.
 * dataAplicacao e observacao podem ser null.
 */
public class Vacina implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int petId;
    private String petNome;
    private String nome;
    private String dataAplicacao;
    private String observacao;
    private boolean aplicada;

    public Vacina() {}

    public int getId() { return id; }
    public int getPetId() { return petId; }
    public String getPetNome() { return petNome != null ? petNome : ""; }
    public String getNome() { return nome != null ? nome : ""; }
    public String getDataAplicacao() { return dataAplicacao; }
    public String getObservacao() { return observacao; }
    public boolean isAplicada() { return aplicada; }

    public boolean isPendente() { return !aplicada; }
    public String getStatusDescricao() { return aplicada ? "Aplicada" : "Pendente"; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Vacina) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}