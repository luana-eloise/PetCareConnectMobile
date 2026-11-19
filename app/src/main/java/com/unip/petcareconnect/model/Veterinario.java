package com.unip.petcareconnect.model;

import java.io.Serializable;

/** Item de GET /api/Veterinarios: {id, nome}. */
public class Veterinario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;

    public Veterinario() {}

    public int getId() { return id; }
    public String getNome() { return nome != null ? nome : ""; }
}