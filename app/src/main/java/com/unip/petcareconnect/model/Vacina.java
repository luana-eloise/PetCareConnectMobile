package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Representa um registro do calendário vacinal do pet do Tutor.
 */
public class Vacina implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int petId;
    private String petNome;
    private String nome;
    private String dose;
    private String lote;
    private String dataAplicacao;
    private String proximaDose;
    private String veterinarioNome;
    private String observacao;
    private boolean aplicada;

    public Vacina() {}

    public Vacina(int id, int petId, String nome, String dataAplicacao, String observacao, boolean aplicada) {
        this.id = id;
        this.petId = petId;
        this.nome = nome != null ? nome.trim() : "";
        this.dataAplicacao = dataAplicacao != null ? dataAplicacao.trim() : null;
        this.observacao = observacao != null ? observacao.trim() : null;
        this.aplicada = aplicada;
    }

    public Vacina(int id, int petId, String petNome, String nome, String dose, String lote, String dataAplicacao, String proximaDose, String veterinarioNome, boolean aplicada) {
        this.id = id;
        this.petId = petId;
        this.petNome = petNome != null ? petNome.trim() : "";
        this.nome = nome != null ? nome.trim() : "";
        this.dose = dose != null ? dose.trim() : "";
        this.lote = lote != null ? lote.trim() : "";
        this.dataAplicacao = dataAplicacao != null ? dataAplicacao.trim() : null;
        this.proximaDose = proximaDose != null ? proximaDose.trim() : null;
        this.veterinarioNome = (veterinarioNome != null && !veterinarioNome.trim().isEmpty()) ? veterinarioNome.trim() : "Clínica PetCare";
        this.aplicada = aplicada;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getNome() {
        return nome != null ? nome : "";
    }

    public void setNome(String nome) {
        this.nome = nome != null ? nome.trim() : "";
    }

    public String getDataAplicacao() {
        return dataAplicacao;
    }

    public void setDataAplicacao(String dataAplicacao) {
        this.dataAplicacao = dataAplicacao != null ? dataAplicacao.trim() : null;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao != null ? observacao.trim() : null;
    }

    public boolean isAplicada() {
        return aplicada;
    }

    public void setAplicada(boolean aplicada) {
        this.aplicada = aplicada;
    }

    public String getPetNome() {
        return petNome != null ? petNome : "";
    }

    public void setPetNome(String petNome) {
        this.petNome = petNome != null ? petNome.trim() : "";
    }

    public String getDose() {
        return dose != null ? dose : "";
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getLote() {
        return lote != null ? lote : "";
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getProximaDose() {
        return proximaDose;
    }

    public void setProximaDose(String proximaDose) {
        this.proximaDose = proximaDose;
    }

    public String getVeterinarioNome() {
        return (veterinarioNome != null && !veterinarioNome.trim().isEmpty()) ? veterinarioNome : "Clínica PetCare";
    }

    public void setVeterinarioNome(String veterinarioNome) {
        this.veterinarioNome = veterinarioNome;
    }

    // ── Auxiliares de Domínio ──

    public boolean isPendente() {
        return !aplicada;
    }

    public String getStatusDescricao() {
        return aplicada ? "Aplicada" : "Pendente";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacina vacina = (Vacina) o;
        return id == vacina.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
