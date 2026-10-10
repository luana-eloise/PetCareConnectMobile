package com.unip.petcareconnect.model;

import java.io.Serializable;

public class Vacina implements Serializable {
    private int id;
    private int petId;
    private String nome;
    private String dataAplicacao;
    private String observacao;
    private boolean aplicada;
    private String petNome;
    private String dose;
    private String lote;
    private String proximaDose;
    private String veterinarioNome;

    public Vacina() {}

    public Vacina(int id, int petId, String nome, String dataAplicacao, String observacao, boolean aplicada) {
        this.id = id;
        this.petId = petId;
        this.nome = nome;
        this.dataAplicacao = dataAplicacao;
        this.observacao = observacao;
        this.aplicada = aplicada;
    }

    public Vacina(int id, int petId, String petNome, String nome, String dose, String lote, String dataAplicacao, String proximaDose, String veterinarioNome, boolean aplicada) {
        this.id = id;
        this.petId = petId;
        this.petNome = petNome;
        this.nome = nome;
        this.dose = dose;
        this.lote = lote;
        this.dataAplicacao = dataAplicacao;
        this.proximaDose = proximaDose;
        this.veterinarioNome = veterinarioNome;
        this.aplicada = aplicada;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(String dataAplicacao) { this.dataAplicacao = dataAplicacao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public boolean isAplicada() { return aplicada; }
    public void setAplicada(boolean aplicada) { this.aplicada = aplicada; }

    public String getPetNome() { return petNome; }
    public void setPetNome(String petNome) { this.petNome = petNome; }

    public String getDose() { return dose; }
    public void setDose(String dose) { this.dose = dose; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public String getProximaDose() { return proximaDose; }
    public void setProximaDose(String proximaDose) { this.proximaDose = proximaDose; }

    public String getVeterinarioNome() { return veterinarioNome; }
    public void setVeterinarioNome(String veterinarioNome) { this.veterinarioNome = veterinarioNome; }

}
