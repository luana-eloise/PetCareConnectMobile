package com.unip.petcareconnect.model;

import java.io.Serializable;

public class Relatorio implements Serializable {
    private int id;
    private int petId;
    private String petNome;
    private String tutorNome;
    private String texto;
    private String status; // 'Disponivel' | 'Pendente'
    private String atualizacao;

    public Relatorio() {}

    public Relatorio(int id, int petId, String petNome, String tutorNome, String texto, String status, String atualizacao) {
        this.id = id;
        this.petId = petId;
        this.petNome = petNome;
        this.tutorNome = tutorNome;
        this.texto = texto;
        this.status = status;
        this.atualizacao = atualizacao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getPetNome() { return petNome; }
    public void setPetNome(String petNome) { this.petNome = petNome; }

    public String getTutorNome() { return tutorNome; }
    public void setTutorNome(String tutorNome) { this.tutorNome = tutorNome; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAtualizacao() { return atualizacao; }
    // Compatibility getters for UI expectations
    // Tipo corresponds to the status of the report (e.g., "Disponivel")
    public String getTipo() {
        return status;
    }

    // Data de emissão corresponds to the atualizacao field
    public String getDataEmissao() {
        return atualizacao;
    }

    // Veterinário responsável corresponds to tutorNome (the vet who authored the report)
    public String getVeterinarioNome() {
        return tutorNome;
    }

    // Diagnóstico is the main text of the report
    public String getDiagnostico() {
        return texto;
    }

    // Conduta may be optional; return empty string if not provided
    public String getConduta() {
        return ""; // placeholder – no separate field defined
    }

    // Título para exibição no diálogo – use a generic label
    public String getTitulo() {
        return "Laudo Clínico";
    }

}
