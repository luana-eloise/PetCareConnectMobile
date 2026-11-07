package com.unip.petcareconnect.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * Pet como a API devolve: {id, nome, tipo, raca, idade, peso, tutorId, tutorNome, criadoEm}.
 * raca, idade e peso podem vir null.
 */
public class Pet implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String tipo;
    private String raca;
    private Integer idade;
    private Double peso;
    private int tutorId;
    private String tutorNome;
    private String criadoEm; // "dd/MM/yyyy HH:mm"

    public Pet() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome != null ? nome : ""; }
    public void setNome(String nome) { this.nome = nome != null ? nome.trim() : ""; }

    public String getTipo() { return tipo != null ? tipo : ""; }
    public void setTipo(String tipo) { this.tipo = tipo != null ? tipo.trim() : ""; }

    // Compatibilidade com código antigo ("especie" = "tipo")
    public String getEspecie() { return getTipo(); }
    public void setEspecie(String especie) { setTipo(especie); }

    /** Vazio quando a API manda null (não inventa "SRD"). */
    public String getRaca() { return raca != null ? raca : ""; }
    public void setRaca(String raca) {
        this.raca = (raca != null && !raca.trim().isEmpty()) ? raca.trim() : null;
    }

    /** Valor real da API; pode ser null. */
    public Integer getIdadeValor() { return idade; }
    public Double getPesoValor() { return peso; }
    public void setIdade(Integer idade) { this.idade = idade; }
    public void setPeso(Double peso) { this.peso = peso; }

    // Compatibilidade: 0 quando null. Para saber se existe, use os "Valor".
    public int getIdade() { return idade != null ? idade : 0; }
    public double getPeso() { return peso != null ? peso : 0.0; }

    public int getTutorId() { return tutorId; }
    public String getTutorNome() { return tutorNome != null ? tutorNome : ""; }
    public String getCriadoEm() { return criadoEm; }

    /** O back só devolve pets existentes. */
    public boolean isAtivo() { return true; }

    public String getPesoFormatado() {
        return peso != null ? String.format(Locale.getDefault(), "%.1f kg", peso) : "Peso não informado";
    }

    public String getIdadeFormatada() {
        if (idade == null) return "Idade não informada";
        if (idade <= 0) return "Menos de 1 ano";
        if (idade == 1) return "1 ano";
        return idade + " anos";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((Pet) o).id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}