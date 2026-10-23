package com.unip.petcareconnect.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Representa o animal de estimação vinculado ao Tutor autenticado.
 */
@SuppressWarnings("unused")
public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;

    @SerializedName(value = "especie", alternate = {"tipo"})
    private String especie;

    private String raca; // Requisito RF-002 do PIM
    private int idade;
    private double peso;
    private int tutorId;
    private String tutorNome;
    private String criadoEm;
    private boolean ativo = true;

    public Pet() {
        this.ativo = true;
    }

    public Pet(int id, String nome, String especie, String raca, int idade, double peso, int tutorId, String tutorNome) {
        this.id = id;
        this.nome = nome != null ? nome.trim() : "";
        this.especie = especie != null ? especie.trim() : "";
        this.raca = (raca != null && !raca.trim().isEmpty()) ? raca.trim() : "SRD";
        this.idade = Math.max(0, idade);
        this.peso = Math.max(0.0, peso);
        this.tutorId = tutorId;
        this.tutorNome = tutorNome != null ? tutorNome.trim() : "";
        this.ativo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome != null ? nome : ""; }
    public void setNome(String nome) { this.nome = nome != null ? nome.trim() : ""; }

    public String getEspecie() { return especie != null ? especie : ""; }
    public void setEspecie(String especie) { this.especie = especie != null ? especie.trim() : ""; }

    public String getTipo() { return getEspecie(); }
    public void setTipo(String tipo) { setEspecie(tipo); }

    public String getRaca() { return (raca != null && !raca.trim().isEmpty()) ? raca : "SRD"; }
    public void setRaca(String raca) { this.raca = (raca != null && !raca.trim().isEmpty()) ? raca.trim() : "SRD"; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = Math.max(0, idade); }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = Math.max(0.0, peso); }

    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }

    public String getTutorNome() { return tutorNome != null ? tutorNome : ""; }
    public void setTutorNome(String tutorNome) { this.tutorNome = tutorNome != null ? tutorNome.trim() : ""; }

    public String getCriadoEm() { return criadoEm; }
    public void setCriadoEm(String criadoEm) { this.criadoEm = criadoEm; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public String getPesoFormatado() {
        return String.format(Locale.getDefault(), "%.1f kg", peso);
    }

    public String getIdadeFormatada() {
        if (idade <= 0) return "Menos de 1 ano";
        if (idade == 1) return "1 ano";
        return idade + " anos";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id == pet.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
