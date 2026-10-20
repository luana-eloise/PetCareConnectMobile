package com.unip.petcareconnect.model;

import java.io.Serializable;

public class Pet implements Serializable {
    private int id;
    private String nome;
    private String tipo;
    private String raca; // Requisito RF-002 do PIM
    private int idade;
    private double peso;
    private int tutorId;
    private String tutorNome;
    private String criadoEm;

    public Pet() {}

    public Pet(int id, String nome, String tipo, String raca, int idade, double peso, int tutorId, String tutorNome) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.raca = raca;
        this.idade = idade;
        this.peso = peso;
        this.tutorId = tutorId;
        this.tutorNome = tutorNome;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getEspecie() { return tipo; }
    public void setEspecie(String especie) { this.tipo = especie; }

    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }


    public String getTutorNome() { return tutorNome; }
    public void setTutorNome(String tutorNome) { this.tutorNome = tutorNome; }

    public String getCriadoEm() { return criadoEm; }
    public void setCriadoEm(String criadoEm) { this.criadoEm = criadoEm; }
}
