package com.unip.petcareconnect.model;

import java.io.Serializable;

public class Aviso implements Serializable {
    private int id;
    private String titulo;
    private String texto;
    private String tipo; // 'geral', 'tutor', 'pet', 'consulta'
    private Integer tutorId;
    private Integer petId;
    private boolean lido;

    public Aviso() {}

    public Aviso(int id, String titulo, String texto, String tipo, Integer tutorId, Integer petId, boolean lido) {
        this.id = id;
        this.titulo = titulo;
        this.texto = texto;
        this.tipo = tipo;
        this.tutorId = tutorId;
        this.petId = petId;
        this.lido = lido;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPrioridade() {
        return tipo;
    }

    public Integer getTutorId() { return tutorId; }
    public void setTutorId(Integer tutorId) { this.tutorId = tutorId; }

    // Returns the date of the notice; not stored in current model, returns empty string for now
    public String getData() {
        return ""; // placeholder – no date field defined
    }

    // Alias for texto field – provides the message content
    public String getMensagem() {
        return texto;
    }

    public void setPetId(Integer petId) { this.petId = petId; }

    public boolean isLido() { return lido; }
    public void setLido(boolean lido) { this.lido = lido; }
}
