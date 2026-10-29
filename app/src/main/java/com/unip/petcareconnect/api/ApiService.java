package com.unip.petcareconnect.api;

import com.unip.petcareconnect.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Contrato de comunicação HTTP/REST com o Backend do PetCare Connect.
 * Focado exclusivamente nos recursos disponíveis para o perfil de Tutor.
 */
public interface ApiService {

    // ── AUTENTICAÇÃO E CADASTRO ──

    @POST("api/Auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest req);

    @POST("api/Auth/cadastro")
    Call<ApiResponse<Integer>> cadastrar(@Body CadastroRequest req);

    // ── GERENCIAMENTO DE PETS ──

    @GET("api/Pets")
    Call<ApiResponse<List<Pet>>> listarPets();

    @GET("api/Pets/{id}")
    Call<ApiResponse<Pet>> buscarPetPorId(@Path("id") int id);

    @POST("api/Pets")
    Call<ApiResponse<Integer>> criarPet(@Body Pet pet);

    @PATCH("api/Pets/{id}")
    Call<ApiResponse<Void>> atualizarPet(@Path("id") int id, @Body Pet pet);

    @DELETE("api/Pets/{id}")
    Call<ApiResponse<Void>> removerPet(@Path("id") int id);

    // ── CONSULTAS VETERINÁRIAS ──

    @GET("api/Consultas")
    Call<ApiResponse<List<Consulta>>> listarConsultas();

    @POST("api/Consultas")
    Call<ApiResponse<Integer>> criarConsulta(@Body Consulta consulta);

    @POST("api/Consultas/{id}/cancelar")
    Call<ApiResponse<Void>> cancelarConsulta(@Path("id") int id);

    // ── CARTEIRA DE VACINAS ──

    @GET("api/Vacinas/{petId}")
    Call<ApiResponse<List<Vacina>>> listarVacinas(@Path("petId") int petId);

    // ── HISTÓRICO CLÍNICO DO PET ──

    @GET("api/Historico/{petId}")
    Call<ApiResponse<List<Historico>>> listarHistorico(@Path("petId") int petId);

    // ── PRONTUÁRIOS E LAUDOS CLÍNICOS ──

    @GET("api/Relatorios")
    Call<ApiResponse<List<Relatorio>>> listarRelatorios(@Query("petId") Integer petId);

    // ── AVISOS E NOTIFICAÇÕES ──

    @GET("api/Avisos")
    Call<ApiResponse<List<Aviso>>> listarAvisos();

    @POST("api/Avisos/{id}/lido")
    Call<ApiResponse<Void>> marcarAvisoLido(@Path("id") int id);
}
