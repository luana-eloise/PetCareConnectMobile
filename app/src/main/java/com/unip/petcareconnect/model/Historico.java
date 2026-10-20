package com.unip.petcareconnect.model;

import java.io.Serializable;

public class Historico implements Serializable {
    private int id;
    private int petId;
    private String descricao;
    private String dataEvento;

    public Historico() {}

    public Historico(int id, int petId, String descricao, String dataEvento) {
        this.id = id;
        this.petId = petId;
        this.descricao = descricao;
        this.dataEvento = dataEvento;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getDataEvento() { return dataEvento; }
    public void setDataEvento(String dataEvento) { this.dataEvento = dataEvento; }
}
