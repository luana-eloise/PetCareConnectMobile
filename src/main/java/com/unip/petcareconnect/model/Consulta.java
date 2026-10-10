package com.unip.petcareconnect.model;

import java.io.Serializable;

public class Consulta implements Serializable {
    private int id;
    private int petId;
    private String petNome;
    private int veterinarioId;
    private String vetNome;
    private String data;
    private String hora;
    private String status; // 'Marcada', 'Realizada', 'Cancelada', 'NaoCompareceu'
    private String motivo;
    private String observacoes;

    public Consulta() {}

    public Consulta(int id, int petId, String petNome, int veterinarioId, String vetNome, String data, String hora, String status, String observacoes) {
        this.id = id;
        this.petId = petId;
        this.petNome = petNome;
        this.veterinarioId = veterinarioId;
        this.vetNome = vetNome;
        this.data = data;
        this.hora = hora;
        this.status = status;
        this.observacoes = observacoes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    private int tutorId;
    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }

    public String getPetNome() { return petNome; }
    public void setPetNome(String petNome) { this.petNome = petNome; }

    public int getVeterinarioId() { return veterinarioId; }
    public void setVeterinarioId(int veterinarioId) { this.veterinarioId = veterinarioId; }

    public String getVetNome() { return vetNome; }
    public void setVetNome(String vetNome) { this.vetNome = vetNome; }

    public String getVeterinarioNome() { return vetNome; }
    public void setVeterinarioNome(String vetNome) { this.vetNome = vetNome; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getHorario() { return hora; }
    public void setHorario(String hora) { this.hora = hora; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

}
