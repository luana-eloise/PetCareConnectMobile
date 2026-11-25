package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Item de GET /api/Avisos:
 * {id, titulo, texto, tipo "consulta"|"pet", tutorId, petId (nullable), lido}.
 * A API não envia data.
 */
public class Aviso implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TIPO_CONSULTA = "consulta";
    public static final String TIPO_PET = "pet";

    private int id;
    private String titulo;
    private String texto;
    private String tipo;
    private Integer tutorId;
    private Integer petId;
    private boolean lido;

    public Aviso() {}

    public int getId() { return id; }
    public String getTitulo() { return titulo != null ? titulo : ""; }
    public String getTexto() { return texto != null ? texto : ""; }
    public String getTipo() { return tipo != null ? tipo : ""; }
    public Integer getTutorId() { return tutorId; }
    public Integer getPetId() { return petId; }
    public boolean isLido() { return lido; }
    public void setLido(boolean lido) { this.lido = lido; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Aviso) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}