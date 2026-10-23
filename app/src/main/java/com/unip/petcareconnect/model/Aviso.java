package com.unip.petcareconnect.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Objects;

/**
 * Notificação ou comunicado enviado pela clínica ou sistema para o Tutor.
 */
public class Aviso implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TIPO_GERAL = "geral";
    public static final String TIPO_TUTOR = "tutor";
    public static final String TIPO_PET = "pet";
    public static final String TIPO_CONSULTA = "consulta";

    private int id;
    private String titulo;

    @SerializedName(value = "texto", alternate = {"mensagem"})
    private String texto;

    private String tipo = TIPO_GERAL; // 'geral', 'tutor', 'pet', 'consulta'
    private Integer tutorId;
    private Integer petId;
    private String data;
    private boolean lido;

    public Aviso() {
        this.tipo = TIPO_GERAL;
    }

    public Aviso(int id, String titulo, String texto, String tipo, Integer tutorId, Integer petId, boolean lido) {
        this.id = id;
        this.titulo = titulo != null ? titulo.trim() : "";
        this.texto = texto != null ? texto.trim() : "";
        this.tipo = (tipo != null && !tipo.trim().isEmpty()) ? tipo.trim() : TIPO_GERAL;
        this.tutorId = tutorId;
        this.petId = petId;
        this.lido = lido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo != null ? titulo : "";
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo != null ? titulo.trim() : "";
    }

    public String getTexto() {
        return texto != null ? texto : "";
    }

    public void setTexto(String texto) {
        this.texto = texto != null ? texto.trim() : "";
    }

    public String getTipo() {
        return tipo != null ? tipo : TIPO_GERAL;
    }

    public void setTipo(String tipo) {
        this.tipo = (tipo != null && !tipo.trim().isEmpty()) ? tipo.trim() : TIPO_GERAL;
    }

    public String getPrioridade() {
        return getTipo();
    }

    public Integer getTutorId() {
        return tutorId;
    }

    public void setTutorId(Integer tutorId) {
        this.tutorId = tutorId;
    }

    public Integer getPetId() {
        return petId;
    }

    public void setPetId(Integer petId) {
        this.petId = petId;
    }

    public String getData() {
        return data != null ? data : "";
    }

    public void setData(String data) {
        this.data = data != null ? data.trim() : "";
    }

    public String getMensagem() {
        return getTexto();
    }

    public void setMensagem(String mensagem) {
        setTexto(mensagem);
    }

    public boolean isLido() {
        return lido;
    }

    public void setLido(boolean lido) {
        this.lido = lido;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aviso aviso = (Aviso) o;
        return id == aviso.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
