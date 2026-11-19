package com.unip.petcareconnect.model;

/**
 * Corpo do POST /api/Consultas. observacoes null não é enviado.
 */
public class ConsultaRequest {
    private final int petId;
    private final int veterinarioId;
    private final String data;
    private final String hora;
    private final String observacoes;

    public ConsultaRequest(int petId, int veterinarioId, String data, String hora, String observacoes) {
        this.petId = petId;
        this.veterinarioId = veterinarioId;
        this.data = data;
        this.hora = hora;
        this.observacoes = (observacoes != null && !observacoes.trim().isEmpty()) ? observacoes.trim() : null;
    }
}