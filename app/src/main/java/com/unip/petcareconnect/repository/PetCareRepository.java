package com.unip.petcareconnect.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unip.petcareconnect.api.ApiClient;
import com.unip.petcareconnect.api.ApiService;
import com.unip.petcareconnect.model.ApiResponse;
import com.unip.petcareconnect.model.Aviso;
import com.unip.petcareconnect.model.CadastroRequest;
import com.unip.petcareconnect.model.Consulta;
import com.unip.petcareconnect.model.Historico;
import com.unip.petcareconnect.model.LoginRequest;
import com.unip.petcareconnect.model.LoginResponse;
import com.unip.petcareconnect.model.Pet;
import com.unip.petcareconnect.model.PetRequest;
import com.unip.petcareconnect.model.Relatorio;
import com.unip.petcareconnect.model.Vacina;
import com.unip.petcareconnect.util.SessionManager;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Repositório central de dados do PetCare Connect Mobile (perfil Tutor).
 * Login e Pets: API-first. Demais áreas ainda usam cache local (passos 3 a 5).
 */
@SuppressWarnings("unused")
public class PetCareRepository {

    private static final String PREF_DATA = "petcare_mobile_local_db";
    public static final String PREFIXO_ERRO_PERFIL = "Esta conta não é de Tutor";
    private static final String MSG_SERVIDOR_ACORDANDO =
            "Servidor acordando, tente novamente em instantes.";

    private final Context context;
    private final SessionManager session;
    private final SharedPreferences dataPref;
    private final Gson gson;

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    public interface RepositoryCallback<T> extends Callback<T> {}

    public PetCareRepository(Context context) {
        this.context = context.getApplicationContext();
        this.session = new SessionManager(context);
        this.dataPref = this.context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        this.gson = new Gson();
        inicializarBaseLocalSeVazia();
    }

    // =========================================================================
    // SEED LOCAL (TEMPORÁRIO: sai nos passos 3 e 4). Pets não têm mais seed.
    // =========================================================================

    private void inicializarBaseLocalSeVazia() {
        if (!dataPref.getBoolean("initialized", false)) {
            List<Consulta> consultas = new ArrayList<>();
            consultas.add(new Consulta(1, 1, "Rex", 2, "Dr. Carlos Mendes", "15/10/2026", "14:00", Consulta.STATUS_MARCADA, "Consulta de rotina anual"));
            consultas.add(new Consulta(2, 2, "Mia", 2, "Dr. Carlos Mendes", "01/09/2026", "10:30", Consulta.STATUS_REALIZADA, "Exame clínico concluído com sucesso"));

            List<Vacina> vacinas = new ArrayList<>();
            vacinas.add(new Vacina(1, 1, "Antirrábica", "10/05/2026", "Dose anual", true));
            vacinas.add(new Vacina(2, 1, "V10 Canina", "10/06/2026", "Reforço anual", true));
            vacinas.add(new Vacina(3, 1, "Giárdia Canina", null, "Reforço pendente", false));
            vacinas.add(new Vacina(4, 2, "V4 Felina", "12/04/2026", "Primeira dose", true));

            List<Historico> historicos = new ArrayList<>();
            historicos.add(new Historico(1, 1, "Consulta de rotina com pesagem e vermifugação.", "10/06/2026"));
            historicos.add(new Historico(2, 1, "Aplicação da vacina antirrábica preventiva.", "10/05/2026"));
            historicos.add(new Historico(3, 2, "Exame dermatológico e odontológico sem alterações.", "01/09/2026"));

            List<Relatorio> relatorios = new ArrayList<>();
            relatorios.add(new Relatorio(1, 1, "Rex", "Maria Oliveira", "Dr. Carlos Mendes",
                    "PARECER CLÍNICO:\nPaciente canino, Labrador Retriever, 3 anos, 25.5 kg.\nExame físico dentro dos parâmetros de normalidade. Carteira de vacinação em dia. Recomendada manutenção de ração super premium.",
                    Relatorio.STATUS_DISPONIVEL, "10/06/2026 15:30"));
            relatorios.add(new Relatorio(2, 2, "Mia", "Maria Oliveira", "Dr. Carlos Mendes",
                    "PARECER CLÍNICO:\nPaciente felino, Siamês, 2 anos, 4.2 kg.\nAusculta cardiopulmonar límpida. Hidratação e reflexos preservados.",
                    Relatorio.STATUS_DISPONIVEL, "01/09/2026 11:15"));

            List<Aviso> avisos = new ArrayList<>();
            avisos.add(new Aviso(1, "Bem-vindo ao PetCare Connect", "O sistema está ativo no seu celular! Cadastre seus pets e acompanhe consultas.", Aviso.TIPO_GERAL, null, null, false));
            avisos.add(new Aviso(2, "Consulta Confirmada", "Consulta de Rex confirmada para 15/10/2026 às 14:00 com Dr. Carlos Mendes.", Aviso.TIPO_CONSULTA, 1, 1, false));
            avisos.add(new Aviso(3, "Vacina Pendente", "A vacina de Giárdia do Rex está pendente para agendamento.", Aviso.TIPO_PET, 1, 1, false));

            salvarLista("consultas", consultas);
            salvarLista("vacinas", vacinas);
            salvarLista("historico", historicos);
            salvarLista("relatorios", relatorios);
            salvarLista("avisos", avisos);

            dataPref.edit().putBoolean("initialized", true).apply();
        }
    }

    private <T> void salvarLista(String chave, List<T> lista) {
        dataPref.edit().putString(chave, gson.toJson(lista)).apply();
    }

    private <T> List<T> obterLista(String chave, Type tipo) {
        String json = dataPref.getString(chave, null);
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        List<T> resultado = gson.fromJson(json, tipo);
        return resultado != null ? resultado : new ArrayList<>();
    }

    // =========================================================================
    // ERROS
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

    // =========================================================================
    // AUTENTICAÇÃO
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

    // =========================================================================
    // CADASTRO (ainda com fallback falso: sai no passo 5)
    // =========================================================================

    public void cadastrarTutor(String nome, String login, String email, String senha, Callback<Boolean> callback) {
        cadastrarTutor(nome, login, email, senha, null, callback);
    }

    public void cadastrarTutor(String nome, String login, String email, String senha, String telefone, Callback<Boolean> callback) {
        ApiService api = ApiClient.getService(context);
        api.cadastrar(new CadastroRequest(nome, login, email, senha, telefone)).enqueue(new retrofit2.Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                callback.onSuccess(true); // TODO passo 5
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onSuccess(true); // TODO passo 5
            }
        });
    }

    public void cadastrarTutor(CadastroRequest req, Callback<Boolean> callback) {
        cadastrarTutor(req.getNome(), req.getLogin(), req.getEmail(), req.getSenha(), req.getTelefone(), callback);
    }

    // =========================================================================
    // PETS (API-first)
    // =========================================================================

    public void listarPets(Callback<List<Pet>> callback) {
        ApiClient.getService(context).listarPets().enqueue(new retrofit2.Callback<ApiResponse<List<Pet>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Pet>>> call, Response<ApiResponse<List<Pet>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    List<Pet> pets = response.body().getDados() != null
                            ? response.body().getDados() : new ArrayList<Pet>();
                    // Cópia só de leitura, usada por consultas/vacinas até os próximos passos
                    salvarLista("pets", pets);
                    callback.onSuccess(pets);
                    return;
                }
                callback.onError(mensagemDeErro(response));
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Pet>>> call, Throwable t) {
                callback.onError(MSG_SERVIDOR_ACORDANDO);
            }
        });
    }

    public List<Pet> obterPetsLocais() {
        Type type = new TypeToken<List<Pet>>(){}.getType();
        return obterLista("pets", type);
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
    // CONSULTAS (ainda local/híbrido: passo 3)
    // =========================================================================

    public void listarConsultas(Callback<List<Consulta>> callback) {
        ApiService api = ApiClient.getService(context);
        api.listarConsultas().enqueue(new retrofit2.Callback<ApiResponse<List<Consulta>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Consulta>>> call, Response<ApiResponse<List<Consulta>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDados() != null) {
                    List<Consulta> lista = response.body().getDados();
                    salvarLista("consultas", lista);
                    callback.onSuccess(lista);
                    return;
                }
                callback.onSuccess(obterConsultasLocais());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Consulta>>> call, Throwable t) {
                callback.onSuccess(obterConsultasLocais());
            }
        });
    }

    public List<Consulta> obterConsultasLocais() {
        Type type = new TypeToken<List<Consulta>>(){}.getType();
        return obterLista("consultas", type);
    }

    public void listarConsultasDoPerfil(Callback<List<Consulta>> callback) {
        listarConsultas(new Callback<List<Consulta>>() {
            @Override
            public void onSuccess(List<Consulta> todas) {
                int tutorId = session.getUserId();
                Set<Integer> idsDosMeusPets = new HashSet<>();

                for (Pet p : obterPetsLocais()) {
                    if (p.getTutorId() == tutorId || tutorId == 0) {
                        idsDosMeusPets.add(p.getId());
                    }
                }

                List<Consulta> filtradas = new ArrayList<>();
                for (Consulta c : todas) {
                    if (idsDosMeusPets.contains(c.getPetId()) || c.getTutorId() == tutorId) {
                        filtradas.add(c);
                    }
                }
                callback.onSuccess(filtradas);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void agendarConsulta(Consulta consulta, Callback<Consulta> callback) {
        List<Consulta> lista = obterConsultasLocais();

        for (Consulta c : lista) {
            if (c.getData().equalsIgnoreCase(consulta.getData()) &&
                    c.getHora().equalsIgnoreCase(consulta.getHora()) &&
                    !c.isCancelada()) {
                callback.onError("Horário já reservado para este veterinário (Regra RN-002).");
                return;
            }
        }

        int novoId = lista.isEmpty() ? 1 : lista.get(lista.size() - 1).getId() + 1;
        consulta.setId(novoId);
        consulta.setStatus(Consulta.STATUS_MARCADA);
        consulta.setTutorId(session.getUserId());
        lista.add(0, consulta);
        salvarLista("consultas", lista);

        ApiService api = ApiClient.getService(context);
        api.criarConsulta(consulta).enqueue(new retrofit2.Callback<ApiResponse<Integer>>() {
            @Override public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {}
            @Override public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {}
        });

        callback.onSuccess(consulta);
    }

    public void cancelarConsulta(int id, Callback<Boolean> callback) {
        List<Consulta> lista = obterConsultasLocais();
        for (Consulta c : lista) {
            if (c.getId() == id) {
                String novoStatus = calcularStatusCancelamento(c.getData(), c.getHora());
                c.setStatus(novoStatus);
                salvarLista("consultas", lista);

                ApiService api = ApiClient.getService(context);
                api.cancelarConsulta(id).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
                    @Override public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}
                    @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
                });

                callback.onSuccess(true);
                return;
            }
        }
        callback.onError("Consulta não encontrada.");
    }

    private String calcularStatusCancelamento(String dataStr, String horaStr) {
        if (dataStr == null || dataStr.trim().isEmpty()) {
            return Consulta.STATUS_CANCELADA;
        }
        try {
            String horaFormatada = (horaStr != null && !horaStr.trim().isEmpty()) ? horaStr.trim() : "00:00";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            sdf.setLenient(false);
            Date dataConsulta = sdf.parse(dataStr.trim() + " " + horaFormatada);

            if (dataConsulta != null) {
                long diffMs = dataConsulta.getTime() - System.currentTimeMillis();
                if (diffMs < 24L * 60L * 60L * 1000L) {
                    return Consulta.STATUS_NAO_COMPARECEU;
                }
            }
        } catch (ParseException ignored) {
        }
        return Consulta.STATUS_CANCELADA;
    }

    // =========================================================================
    // VACINAS, HISTÓRICO, RELATÓRIOS E AVISOS (ainda locais: passo 4)
    // =========================================================================

    public void listarTodasVacinas(Callback<List<Vacina>> callback) {
        Type typeVacina = new TypeToken<List<Vacina>>(){}.getType();
        Type typePet = new TypeToken<List<Pet>>(){}.getType();
        List<Vacina> vacinas = obterLista("vacinas", typeVacina);
        List<Pet> pets = obterLista("pets", typePet);
        preencherPetNome(vacinas, pets);
        callback.onSuccess(vacinas);
    }

    public void listarVacinasDoPet(int petId, Callback<List<Vacina>> callback) {
        Type typeVacina = new TypeToken<List<Vacina>>(){}.getType();
        Type typePet = new TypeToken<List<Pet>>(){}.getType();
        List<Vacina> todas = obterLista("vacinas", typeVacina);
        List<Pet> pets = obterLista("pets", typePet);
        List<Vacina> doPet = new ArrayList<>();
        for (Vacina v : todas) {
            if (v.getPetId() == petId) {
                doPet.add(v);
            }
        }
        preencherPetNome(doPet, pets);
        callback.onSuccess(doPet);
    }

    private void preencherPetNome(List<Vacina> vacinas, List<Pet> pets) {
        if (vacinas == null || pets == null) return;
        Map<Integer, String> nomePorId = new HashMap<>();
        for (Pet p : pets) {
            nomePorId.put(p.getId(), p.getNome());
        }
        for (Vacina v : vacinas) {
            if (v.getPetNome() == null || v.getPetNome().isEmpty()) {
                String nome = nomePorId.get(v.getPetId());
                if (nome != null) v.setPetNome(nome);
            }
        }
    }

    public void listarVacinas(int petId, Callback<List<Vacina>> callback) {
        listarVacinasDoPet(petId, callback);
    }

    public void listarHistorico(int petId, Callback<List<Historico>> callback) {
        Type type = new TypeToken<List<Historico>>(){}.getType();
        List<Historico> todos = obterLista("historico", type);
        List<Historico> doPet = new ArrayList<>();
        for (Historico h : todos) {
            if (h.getPetId() == petId) {
                doPet.add(h);
            }
        }
        callback.onSuccess(doPet);
    }

    public void listarRelatorios(Callback<List<Relatorio>> callback) {
        Type type = new TypeToken<List<Relatorio>>(){}.getType();
        callback.onSuccess(obterLista("relatorios", type));
    }

    public void listarAvisos(Callback<List<Aviso>> callback) {
        ApiService api = ApiClient.getService(context);
        api.listarAvisos().enqueue(new retrofit2.Callback<ApiResponse<List<Aviso>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Aviso>>> call, Response<ApiResponse<List<Aviso>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDados() != null) {
                    List<Aviso> remotos = response.body().getDados();
                    salvarLista("avisos", remotos);
                    callback.onSuccess(remotos);
                    return;
                }
                Type type = new TypeToken<List<Aviso>>(){}.getType();
                callback.onSuccess(obterLista("avisos", type));
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Aviso>>> call, Throwable t) {
                Type type = new TypeToken<List<Aviso>>(){}.getType();
                callback.onSuccess(obterLista("avisos", type));
            }
        });
    }

    public void marcarAvisoLido(int avisoId, Callback<Boolean> callback) {
        atualizarStatusAviso(avisoId, true, callback);
    }

    public void marcarAvisoNaoLido(int avisoId, Callback<Boolean> callback) {
        atualizarStatusAviso(avisoId, false, callback);
    }

    public void atualizarStatusAviso(int avisoId, boolean lido, Callback<Boolean> callback) {
        Type type = new TypeToken<List<Aviso>>(){}.getType();
        List<Aviso> avisos = obterLista("avisos", type);
        for (Aviso a : avisos) {
            if (a.getId() == avisoId) {
                a.setLido(lido);
                break;
            }
        }
        salvarLista("avisos", avisos);

        if (lido) {
            ApiService api = ApiClient.getService(context);
            api.marcarAvisoLido(avisoId).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
                @Override public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}
                @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
            });
        }

        callback.onSuccess(true);
    }
}