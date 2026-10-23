package com.unip.petcareconnect.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa o prontuário ou laudo clínico emitido para o pet do Tutor.
 */
@SuppressWarnings("unused")
public class Relatorio implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_DISPONIVEL = "Disponivel";
    public static final String STATUS_PENDENTE = "Pendente";

    private int id;
    private int petId;
    private String petNome;
    private String tutorNome;
    private String veterinarioNome;

    @SerializedName(value = "texto", alternate = {"diagnostico"})
    private String texto;

    private String status;

    @SerializedName(value = "atualizacao", alternate = {"dataEmissao"})
    private String atualizacao;

    private String conduta;

    public Relatorio() {
        this.status = STATUS_DISPONIVEL;
    }

    public Relatorio(int id, int petId, String petNome, String tutorNome, String texto, String status, String atualizacao) {
        this.id = id;
        this.petId = petId;
        this.petNome = petNome != null ? petNome.trim() : "";
        this.tutorNome = tutorNome != null ? tutorNome.trim() : "";
        this.texto = texto != null ? texto.trim() : "";
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : STATUS_DISPONIVEL;
        this.atualizacao = atualizacao != null ? atualizacao.trim() : "";
        this.veterinarioNome = "Clínica PetCare";
    }

    public Relatorio(int id, int petId, String petNome, String tutorNome, String veterinarioNome, String texto, String status, String atualizacao) {
        this.id = id;
        this.petId = petId;
        this.petNome = petNome != null ? petNome.trim() : "";
        this.tutorNome = tutorNome != null ? tutorNome.trim() : "";
        this.veterinarioNome = (veterinarioNome != null && !veterinarioNome.trim().isEmpty()) ? veterinarioNome.trim() : "Clínica PetCare";
        this.texto = texto != null ? texto.trim() : "";
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : STATUS_DISPONIVEL;
        this.atualizacao = atualizacao != null ? atualizacao.trim() : "";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetNome() { return petNome != null ? petNome : ""; }
    public void setPetNome(String petNome) { this.petNome = petNome != null ? petNome.trim() : ""; }

    public String getTutorNome() { return tutorNome != null ? tutorNome : ""; }
    public void setTutorNome(String tutorNome) { this.tutorNome = tutorNome != null ? tutorNome.trim() : ""; }

    public String getTexto() { return texto != null ? texto : ""; }
    public void setTexto(String texto) { this.texto = texto != null ? texto.trim() : ""; }

    public String getStatus() { return status != null ? status : STATUS_DISPONIVEL; }
    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : STATUS_DISPONIVEL;
    }

    public String getAtualizacao() { return atualizacao != null ? atualizacao : ""; }
    public void setAtualizacao(String atualizacao) { this.atualizacao = atualizacao != null ? atualizacao.trim() : ""; }

    public String getVeterinarioNome() {
        if (veterinarioNome != null && !veterinarioNome.trim().isEmpty()) {
            return veterinarioNome;
        }
        return "Clínica PetCare";
    }

    public void setVeterinarioNome(String veterinarioNome) {
        this.veterinarioNome = veterinarioNome != null ? veterinarioNome.trim() : "Clínica PetCare";
    }

    public String getTipo() { return getStatus(); }
    public String getDataEmissao() { return getAtualizacao(); }
    public String getDiagnostico() { return getTexto(); }
    public String getConduta() { return conduta != null ? conduta : ""; }
    public void setConduta(String conduta) { this.conduta = conduta; }
    public String getTitulo() { return "Laudo Clínico"; }
    public boolean isDisponivel() { return STATUS_DISPONIVEL.equalsIgnoreCase(status); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relatorio relatorio = (Relatorio) o;
        return id == relatorio.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
