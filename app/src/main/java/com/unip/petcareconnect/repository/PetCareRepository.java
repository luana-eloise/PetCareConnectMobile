package com.unip.petcareconnect.repository;

import android.content.Context;

import com.google.gson.Gson;
import com.unip.petcareconnect.api.ApiClient;
import com.unip.petcareconnect.api.ApiService;
import com.unip.petcareconnect.model.ApiResponse;
import com.unip.petcareconnect.model.Aviso;
import com.unip.petcareconnect.model.CadastroRequest;
import com.unip.petcareconnect.model.Consulta;
import com.unip.petcareconnect.model.ConsultaRequest;
import com.unip.petcareconnect.model.Historico;
import com.unip.petcareconnect.model.LoginRequest;
import com.unip.petcareconnect.model.LoginResponse;
import com.unip.petcareconnect.model.Pet;
import com.unip.petcareconnect.model.PetRequest;
import com.unip.petcareconnect.model.Relatorio;
import com.unip.petcareconnect.model.Vacina;
import com.unip.petcareconnect.model.Veterinario;
import com.unip.petcareconnect.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Repositório do PetCare Connect Mobile (perfil Tutor).
 * Todas as áreas são API-first: a API é a única fonte de dados e as regras de negócio
 * (RN-002, RN-005 etc.) são do servidor. Não há seed nem cache local.
 */
public class PetCareRepository {

    public static final String PREFIXO_ERRO_PERFIL = "Esta conta não é de Tutor";
    private static final String MSG_SERVIDOR_ACORDANDO =
            "Servidor acordando, tente novamente em instantes.";
    // Banco local das versões antigas do app: é apagado para não sobrar dado de exemplo
    private static final String PREF_LEGADO = "petcare_mobile_local_db";

    private final Context context;
    private final SessionManager session;
    private final Gson gson;

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public interface RepositoryCallback<T> extends Callback<T> {}

    public PetCareRepository(Context context) {
        this.context = context.getApplicationContext();
        this.session = new SessionManager(context);
        this.gson = new Gson();
        this.context.getSharedPreferences(PREF_LEGADO, Context.MODE_PRIVATE).edit().clear().apply();
    }

    // =========================================================================
    // APOIO
    // =========================================================================

    /**
     * Mensagem de erro: envelope da API quando existir; 5xx vira "servidor acordando"
     * (o Render responde 502/503 durante o boot).
     */
    private <T> String mensagemDeErro(Response<ApiResponse<T>> response) {
        int code = response.code();
        if (code >= 500) {
            return MSG_SERVIDOR_ACORDANDO;
        }
        try {
            if (response.errorBody() != null) {
                String json = response.errorBody().string();
                if (json != null && !json.trim().isEmpty()) {
                    ApiResponse<?> envelope = gson.fromJson(json, ApiResponse.class);
                    if (envelope != null && !envelope.getMensagem().isEmpty()) {
                        return envelope.getMensagem();
                    }
                }
            }
        } catch (Exception ignored) {
            // corpo não era JSON do envelope
        }
        if (response.body() != null && !response.body().getMensagem().isEmpty()) {
            return response.body().getMensagem();
        }
        return "Não foi possível concluir a operação (HTTP " + code + ").";
    }

    /** Callback padrão de leitura: sucesso só com "sucesso: true", senão devolve a mensagem do servidor. */
    private <T> retrofit2.Callback<ApiResponse<T>> aoReceber(final Callback<T> callback, final T vazio) {
        return new retrofit2.Callback<ApiResponse<T>>() {
            @Override
            public void onResponse(Call<ApiResponse<T>> call, Response<ApiResponse<T>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    T dados = response.body().getDados();
                    callback.onSuccess(dados != null ? dados : vazio);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<T>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        };
    }

    // =========================================================================
    // AUTENTICAÇÃO E CADASTRO
    // =========================================================================

    public void login(String login, String senha, Callback<LoginResponse> callback) {
        ApiService api = ApiClient.getService(context);
        api.login(new LoginRequest(login, senha)).enqueue(new retrofit2.Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()
                        && response.body().getDados() != null) {
                    processarLoginComSucesso(response.body().getDados(), callback);
                    return;
                }
                callback.onError(mensagemDeErro(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                // Timeout, sem internet, DNS ou conexão recusada
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    public void login(String login, String senha, String perfil, Callback<LoginResponse> callback) {
        login(login, senha, callback);
    }

    private void processarLoginComSucesso(LoginResponse data, Callback<LoginResponse> callback) {
        String perfil = data.getPerfil();
        boolean ehTutor = perfil != null && SessionManager.PERFIL_TUTOR.equalsIgnoreCase(perfil.trim());
        if (!ehTutor || data.getToken() == null || data.getToken().trim().isEmpty()) {
            // Não salva token
            callback.onError(PREFIXO_ERRO_PERFIL + ". Use o portal web.");
            return;
        }
        session.salvarSessao(data.getToken(), data.getUsuarioId(), data.getNome(), data.getLogin(), perfil);
        callback.onSuccess(data);
    }

    /** Só dá sucesso se a API criar o Tutor. Erros 400 trazem a mensagem do servidor. */
    public void cadastrarTutor(CadastroRequest req, Callback<Boolean> callback) {
        ApiClient.getService(context).cadastrar(req).enqueue(new retrofit2.Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    callback.onSuccess(true);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    // =========================================================================
    // PETS
    // =========================================================================

    public void listarPets(Callback<List<Pet>> callback) {
        ApiClient.getService(context).listarPets().enqueue(aoReceber(callback, new ArrayList<Pet>()));
    }

    /** Só dá sucesso se a API criar. A tela recarrega a lista pelo GET depois. */
    public void adicionarPet(Pet pet, Callback<Pet> callback) {
        ApiClient.getService(context).criarPet(new PetRequest(pet)).enqueue(new retrofit2.Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    callback.onSuccess(pet);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    /** Envia o pet completo (o back não confirmou o que acontece com campo ausente no PATCH). */
    public void atualizarPet(Pet pet, Callback<Boolean> callback) {
        ApiClient.getService(context).atualizarPet(pet.getId(), new PetRequest(pet)).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    callback.onSuccess(true);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    public void excluirPet(int petId, Callback<Boolean> callback) {
        ApiClient.getService(context).removerPet(petId).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    callback.onSuccess(true);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    // =========================================================================
    // VETERINÁRIOS E CONSULTAS
    // =========================================================================

    public void listarVeterinarios(Callback<List<Veterinario>> callback) {
        ApiClient.getService(context).listarVeterinarios().enqueue(aoReceber(callback, new ArrayList<Veterinario>()));
    }

    public void listarConsultas(Callback<List<Consulta>> callback) {
        ApiClient.getService(context).listarConsultas().enqueue(aoReceber(callback, new ArrayList<Consulta>()));
    }

    /** A API já devolve só as consultas do Tutor logado. */
    public void listarConsultasDoPerfil(Callback<List<Consulta>> callback) {
        listarConsultas(callback);
    }

    public void listarHorariosDisponiveis(int veterinarioId, String data, Callback<List<String>> callback) {
        ApiClient.getService(context).horariosDisponiveis(veterinarioId, data)
                .enqueue(aoReceber(callback, new ArrayList<String>()));
    }

    /** RN-002 e demais regras vêm do servidor. Devolve a mensagem do envelope. */
    public void agendarConsulta(int petId, int veterinarioId, String data, String hora,
                                String observacoes, Callback<String> callback) {
        ConsultaRequest req = new ConsultaRequest(petId, veterinarioId, data, hora, observacoes);
        ApiClient.getService(context).criarConsulta(req).enqueue(new retrofit2.Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    String msg = response.body().getMensagem();
                    callback.onSuccess(msg.isEmpty() ? "Consulta agendada com sucesso." : msg);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    /** RN-005 é aplicada pelo servidor. Devolve a mensagem do envelope. */
    public void cancelarConsulta(int id, Callback<String> callback) {
        ApiClient.getService(context).cancelarConsulta(id).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    String msg = response.body().getMensagem();
                    callback.onSuccess(msg.isEmpty() ? "Consulta cancelada." : msg);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    // =========================================================================
    // VACINAS, HISTÓRICO, RELATÓRIOS E AVISOS
    // =========================================================================

    public void listarVacinasDoPet(int petId, Callback<List<Vacina>> callback) {
        ApiClient.getService(context).listarVacinas(petId).enqueue(aoReceber(callback, new ArrayList<Vacina>()));
    }

    public void listarVacinas(int petId, Callback<List<Vacina>> callback) {
        listarVacinasDoPet(petId, callback);
    }

    /** Não há endpoint para "todas": busca os pets e junta as vacinas de cada um. */
    public void listarTodasVacinas(Callback<List<Vacina>> callback) {
        listarPets(new Callback<List<Pet>>() {
            @Override
            public void onSuccess(List<Pet> pets) {
                if (pets.isEmpty()) {
                    callback.onSuccess(new ArrayList<Vacina>());
                    return;
                }
                final List<Vacina> todas = new ArrayList<>();
                final int[] restantes = {pets.size()};
                final boolean[] falhou = {false};
                for (Pet pet : pets) {
                    listarVacinasDoPet(pet.getId(), new Callback<List<Vacina>>() {
                        @Override
                        public void onSuccess(List<Vacina> vacinas) {
                            if (falhou[0]) return;
                            todas.addAll(vacinas);
                            restantes[0]--;
                            if (restantes[0] == 0) callback.onSuccess(todas);
                        }

                        @Override
                        public void onError(String message) {
                            if (falhou[0]) return;
                            falhou[0] = true;
                            callback.onError(message);
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void listarHistorico(int petId, Callback<List<Historico>> callback) {
        ApiClient.getService(context).listarHistorico(petId).enqueue(aoReceber(callback, new ArrayList<Historico>()));
    }

    public void listarRelatorios(Callback<List<Relatorio>> callback) {
        ApiClient.getService(context).listarRelatorios(null).enqueue(aoReceber(callback, new ArrayList<Relatorio>()));
    }

    public void listarAvisos(Callback<List<Aviso>> callback) {
        ApiClient.getService(context).listarAvisos().enqueue(aoReceber(callback, new ArrayList<Aviso>()));
    }

    /** Só confirma se a API confirmar. A API não tem "marcar como não lido". */
    public void marcarAvisoLido(int avisoId, Callback<Boolean> callback) {
        ApiClient.getService(context).marcarAvisoLido(avisoId).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    callback.onSuccess(true);
                } else {
                    callback.onError(mensagemDeErro(response));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }
}