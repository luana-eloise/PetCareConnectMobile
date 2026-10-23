package com.unip.petcareconnect.model;

import java.io.Serializable;

/**
 * Resposta padrão envelopada das APIs REST do PetCare Connect.
 *
 * @param <T> Tipo do payload de dados retornado.
 */
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean sucesso;
    private String mensagem;
    private T dados;

    public ApiResponse() {}

    public ApiResponse(boolean sucesso, String mensagem, T dados) {
        this.sucesso = sucesso;
        this.mensagem = mensagem != null ? mensagem.trim() : "";
        this.dados = dados;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem != null ? mensagem : "";
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem != null ? mensagem.trim() : "";
    }

    public T getDados() {
        return dados;
    }

    public void setDados(T dados) {
        this.dados = dados;
    }
}
