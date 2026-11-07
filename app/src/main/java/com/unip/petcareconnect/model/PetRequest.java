package com.unip.petcareconnect.model;

/**
 * Corpo do POST e do PATCH /api/Pets: só nome, tipo, raca, idade e peso.
 * Campos null não são enviados (o Gson os omite). Não envia tutorId.
 */
public class PetRequest {
    private final String nome;
    private final String tipo;
    private final String raca;
    private final Integer idade;
    private final Double peso;

    public PetRequest(Pet pet) {
        this.nome = pet.getNome();
        this.tipo = pet.getTipo();
        this.raca = pet.getRaca().isEmpty() ? null : pet.getRaca();
        this.idade = pet.getIdadeValor();
        this.peso = pet.getPesoValor();
    }
}