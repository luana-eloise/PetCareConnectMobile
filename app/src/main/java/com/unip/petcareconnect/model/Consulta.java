package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Consulta como a API devolve:
 * {id, petId, petNome, veterinarioId, vetNome, data "dd/MM/yyyy", hora "HH:mm", status, observacoes}.
 */
public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_MARCADA = "Marcada";
    public static final String STATUS_REALIZADA = "Realizada";
    public static final String STATUS_CANCELADA = "Cancelada";
    public static final String STATUS_NAO_COMPARECEU = "NaoCompareceu";

    private int id;
    private int petId;
    private String petNome;
    private int veterinarioId;
    private String vetNome;
    private String data;
    private String hora;
    private String status;
    private String observacoes; // pode ser null

    public Consulta() {}

    public int getId() { return id; }
    public int getPetId() { return petId; }
    public String getPetNome() { return petNome != null ? petNome : ""; }
    public int getVeterinarioId() { return veterinarioId; }
    public String getVetNome() { return vetNome != null ? vetNome : ""; }
    public String getVeterinarioNome() { return getVetNome(); }
    public String getData() { return data != null ? data : ""; }
    public String getHora() { return hora != null ? hora : ""; }
    public String getHorario() { return getHora(); }
    public String getStatus() { return status != null ? status : ""; }
    public String getObservacoes() { return observacoes; }

    /** Texto amigável para a tela (o valor da API é "NaoCompareceu"). */
    public String getStatusFormatado() {
        return isNaoCompareceu() ? "Não compareceu" : getStatus();
    }

    public boolean isMarcada() { return STATUS_MARCADA.equals(status); }
    public boolean isRealizada() { return STATUS_REALIZADA.equals(status); }
    public boolean isCancelada() { return STATUS_CANCELADA.equals(status); }
    public boolean isNaoCompareceu() { return STATUS_NAO_COMPARECEU.equals(status); }

    public String getDataHoraFormatada() {
        if (getData().isEmpty()) return "";
        if (getHora().isEmpty()) return getData();
        return getData() + " às " + getHora();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Consulta) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}