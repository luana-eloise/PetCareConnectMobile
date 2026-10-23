package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Registra um evento relevante na linha do tempo clínica do pet.
 */
public class Historico implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int petId;
    private String descricao;
    private String dataEvento;

    public Historico() {}

    public Historico(int id, int petId, String descricao, String dataEvento) {
        this.id = id;
        this.petId = petId;
        this.descricao = descricao != null ? descricao.trim() : "";
        this.dataEvento = dataEvento != null ? dataEvento.trim() : "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getDescricao() {
        return descricao != null ? descricao : "";
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao != null ? descricao.trim() : "";
    }

    public String getDataEvento() {
        return dataEvento != null ? dataEvento : "";
    }

    public void setDataEvento(String dataEvento) {
        this.dataEvento = dataEvento != null ? dataEvento.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Historico historico = (Historico) o;
        return id == historico.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
