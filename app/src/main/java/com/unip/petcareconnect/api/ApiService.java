package com.unip.petcareconnect.api;

import com.unip.petcareconnect.model.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * Contrato com o back-end do PetCare Connect (perfil Tutor).
 */
public interface ApiService {

    // ── AUTENTICAÇÃO E CADASTRO ──

    @POST("api/Auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest req);

    @POST("api/Auth/cadastro")
    Call<ApiResponse<Integer>> cadastrar(@Body CadastroRequest req);

    // ── PETS ──

    @GET("api/Pets")
    Call<ApiResponse<List<Pet>>> listarPets();

    @GET("api/Pets/{id}")
    Call<ApiResponse<Pet>> buscarPetPorId(@Path("id") int id);

    @POST("api/Pets")
    Call<ApiResponse<Integer>> criarPet(@Body PetRequest pet);

    @PATCH("api/Pets/{id}")
    Call<ApiResponse<Void>> atualizarPet(@Path("id") int id, @Body PetRequest pet);

    @DELETE("api/Pets/{id}")
    Call<ApiResponse<Void>> removerPet(@Path("id") int id);

    // ── VETERINÁRIOS ──

    @GET("api/Veterinarios")
    Call<ApiResponse<List<Veterinario>>> listarVeterinarios();

    // ── CONSULTAS ──

    @GET("api/Consultas")
    Call<ApiResponse<List<Consulta>>> listarConsultas();

    @GET("api/Consultas/horarios-disponiveis")
    Call<ApiResponse<List<String>>> horariosDisponiveis(
            @Query("veterinarioId") int veterinarioId,
            @Query("data") String data);

    @POST("api/Consultas")
    Call<ApiResponse<Integer>> criarConsulta(@Body ConsultaRequest consulta);

    @POST("api/Consultas/{id}/cancelar")
    Call<ApiResponse<Void>> cancelarConsulta(@Path("id") int id);

    // ── VACINAS / HISTÓRICO / RELATÓRIOS / AVISOS (passo 4) ──

    @GET("api/Vacinas/{petId}")
    Call<ApiResponse<List<Vacina>>> listarVacinas(@Path("petId") int petId);

    @GET("api/Historico/{petId}")
    Call<ApiResponse<List<Historico>>> listarHistorico(@Path("petId") int petId);

    @GET("api/Relatorios")
    Call<ApiResponse<List<Relatorio>>> listarRelatorios(@Query("petId") Integer petId);

    @GET("api/Avisos")
    Call<ApiResponse<List<Aviso>>> listarAvisos();

    @POST("api/Avisos/{id}/lido")
    Call<ApiResponse<Void>> marcarAvisoLido(@Path("id") int id);
}