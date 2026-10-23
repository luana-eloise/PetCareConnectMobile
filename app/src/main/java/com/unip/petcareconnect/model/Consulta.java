package com.unip.petcareconnect.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

/**
 * Representa uma consulta veterinária agendada para o pet do Tutor.
 */
@SuppressWarnings("unused")
public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_MARCADA = "Marcada";
    public static final String STATUS_REALIZADA = "Realizada";
    public static final String STATUS_CANCELADA = "Cancelada";
    public static final String STATUS_NAO_COMPARECEU = "NaoCompareceu";

    private int id;
    private int petId;
    private String petNome;
    private int tutorId;
    private int veterinarioId;

    @SerializedName(value = "vetNome", alternate = {"veterinarioNome"})
    private String vetNome;

    private String data; // Formato padrão: dd/MM/yyyy

    @SerializedName(value = "hora", alternate = {"horario"})
    private String hora; // Formato padrão: HH:mm

    private String status;
    private String motivo;
    private String observacoes;

    public Consulta() {
        this.status = STATUS_MARCADA;
    }

    public Consulta(int id, int petId, String petNome, int veterinarioId, String vetNome, String data, String hora, String status, String observacoes) {
        this.id = id;
        this.petId = petId;
        this.petNome = petNome != null ? petNome.trim() : "";
        this.veterinarioId = veterinarioId;
        this.vetNome = vetNome != null ? vetNome.trim() : "Veterinário de Plantão";
        this.data = data != null ? data.trim() : "";
        this.hora = hora != null ? hora.trim() : "";
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : STATUS_MARCADA;
        this.observacoes = observacoes != null ? observacoes.trim() : null;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }

    public String getPetNome() { return petNome != null ? petNome : ""; }
    public void setPetNome(String petNome) { this.petNome = petNome != null ? petNome.trim() : ""; }

    public int getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(int veterinarioId) { this.veterinarioId = veterinarioId; }

    public String getVetNome() {
        return (vetNome != null && !vetNome.trim().isEmpty()) ? vetNome : "Veterinário de Plantão";
    }

    public void setVetNome(String vetNome) {
        this.vetNome = vetNome != null ? vetNome.trim() : "Veterinário de Plantão";
    }

    public String getVeterinarioNome() { return getVetNome(); }
    public void setVeterinarioNome(String vetNome) { setVetNome(vetNome); }

    public String getData() { return data != null ? data : ""; }
    public void setData(String data) { this.data = data != null ? data.trim() : ""; }

    public String getHora() { return hora != null ? hora : ""; }
    public void setHora(String hora) { this.hora = hora != null ? hora.trim() : ""; }

    public String getHorario() { return getHora(); }
    public void setHorario(String horario) { setHora(horario); }

    public String getStatus() { return status != null ? status : STATUS_MARCADA; }
    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : STATUS_MARCADA;
    }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public boolean isMarcada() {
        return STATUS_MARCADA.equalsIgnoreCase(status)
                || "Agendada".equalsIgnoreCase(status)
                || "Confirmada".equalsIgnoreCase(status);
    }

    public boolean isRealizada() { return STATUS_REALIZADA.equalsIgnoreCase(status); }
    public boolean isCancelada() { return STATUS_CANCELADA.equalsIgnoreCase(status); }
    public boolean isNaoCompareceu() { return STATUS_NAO_COMPARECEU.equalsIgnoreCase(status); }

    public String getDataHoraFormatada() {
        if (data == null || data.isEmpty()) return "";
        if (hora == null || hora.isEmpty()) return data;
        return data + " às " + hora;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consulta consulta = (Consulta) o;
        return id == consulta.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
