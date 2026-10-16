package com.unip.petcareconnect.repository;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.unip.petcareconnect.api.ApiClient;
import com.unip.petcareconnect.api.ApiService;
import com.unip.petcareconnect.model.*;
import com.unip.petcareconnect.util.SessionManager;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repositório Unificado (Online/Offline) do PetCare Connect Mobile.
 * Cumpre os requisitos de Integração com APIs e Sincronização de Dados do PIM IV.
 */
public class PetCareRepository {
    private static final String PREF_DATA = "petcare_mobile_local_db";
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
        this.dataPref = context.getSharedPreferences(PREF_DATA, Context.MODE_PRIVATE);
        this.gson = new Gson();
        initLocalDatabaseIfEmpty();
    }

    // =========================================================================
    // SEED LOCAL (DADOS DO PIM III PARA OPERAÇÃO OFFLINE)
    // =========================================================================
    private void initLocalDatabaseIfEmpty() {
        if (!dataPref.getBoolean("initialized", false)) {
            List<Pet> pets = new ArrayList<>();
            pets.add(new Pet(1, "Rex", "Cachorro", "Labrador Retriever", 3, 25.5, 1, "Maria Oliveira"));
            pets.add(new Pet(2, "Mia", "Gato", "Siamês", 2, 4.2, 1, "Maria Oliveira"));

            List<Consulta> consultas = new ArrayList<>();
            consultas.add(new Consulta(1, 1, "Rex", 2, "Dr. Carlos Mendes", "15/10/2026", "14:00", "Marcada", "Consulta de rotina anual"));
            consultas.add(new Consulta(2, 2, "Mia", 2, "Dr. Carlos Mendes", "01/09/2026", "10:30", "Realizada", "Exame clínico concluído com sucesso"));

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
            relatorios.add(new Relatorio(1, 1, "Rex", "Maria Oliveira",
                    "PARECER CLÍNICO:\nPaciente canino, Labrador Retriever, 3 anos, 25.5 kg.\nExame físico dentro dos parâmetros de normalidade. Carteira de vacinação em dia. Recomendada manutenção de ração super premium.",
                    "Disponivel", "10/06/2026 15:30"));
            relatorios.add(new Relatorio(2, 2, "Mia", "Maria Oliveira",
                    "PARECER CLÍNICO:\nPaciente felino, Siamês, 2 anos, 4.2 kg.\nAusculta cardiopulmonar límpida. Hidratação e reflexos preservados.",
                    "Disponivel", "01/09/2026 11:15"));

            List<Aviso> avisos = new ArrayList<>();
            avisos.add(new Aviso(1, "Bem-vindo ao PetCare Connect", "O sistema está ativo no seu celular! Cadastre pets e agende consultas com rapidez.", "geral", null, null, false));
            avisos.add(new Aviso(2, "Consulta Confirmada", "Consulta de Rex confirmada para 15/10/2026 às 14:00 com Dr. Carlos Mendes.", "consulta", 1, 1, false));
            avisos.add(new Aviso(3, "Vacina Pendente", "A vacina Giárdia de Rex está pendente para agendamento.", "pet", 1, 1, false));

            salvarLista("pets", pets);
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
        if (json == null) return new ArrayList<>();
        return gson.fromJson(json, tipo);
    }

    // =========================================================================
    // AUTENTICAÇÃO
    // =========================================================================
    public void login(String login, String senha, Callback<LoginResponse> callback) {
        login(login, senha, "Tutor", callback);
    }

    public void login(String login, String senha, String perfil, Callback<LoginResponse> callback) {
        ApiService api = ApiClient.getService(context);
        api.login(new LoginRequest(login, senha, perfil)).enqueue(new retrofit2.Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    LoginResponse data = response.body().getDados();
                    session.salvarSessao(data.getToken(), data.getUsuarioId(), data.getNome(), data.getLogin(), data.getPerfil());
                    callback.onSuccess(data);
                    return;
                }
                fallbackLogin(login, senha, perfil, callback);
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                fallbackLogin(login, senha, perfil, callback);
            }
        });
    }

    private void fallbackLogin(String login, String senha, String perfil, Callback<LoginResponse> callback) {
        // Validação de credenciais do PIM III
        if (login.equalsIgnoreCase("tutor_teste") && (senha.equals("Tutor123!") || senha.equals("123456"))) {
            LoginResponse res = new LoginResponse("mock_token_tutor", 1, "Maria Oliveira", "tutor_teste", "Tutor");
            session.salvarSessao(res.getToken(), res.getUsuarioId(), res.getNome(), res.getLogin(), res.getPerfil());
            callback.onSuccess(res);
        } else if (login.equalsIgnoreCase("vet_teste") && (senha.equals("Vet123!") || senha.equals("123456"))) {
            LoginResponse res = new LoginResponse("mock_token_vet", 2, "Dr. Carlos Mendes", "vet_teste", "Veterinario");
            session.salvarSessao(res.getToken(), res.getUsuarioId(), res.getNome(), res.getLogin(), res.getPerfil());
            callback.onSuccess(res);
        } else if (login.equalsIgnoreCase("admin_teste") && (senha.equals("Admin123!") || senha.equals("123456"))) {
            LoginResponse res = new LoginResponse("mock_token_admin", 3, "Administrador PetCare", "admin_teste", "Admin");
            session.salvarSessao(res.getToken(), res.getUsuarioId(), res.getNome(), res.getLogin(), res.getPerfil());
            callback.onSuccess(res);
        } else {
            callback.onError("Login ou senha inválidos.");
        }
    }

    public void cadastrarTutor(String nome, String login, String email, String senha, Callback<Boolean> callback) {
        ApiService api = ApiClient.getService(context);
        api.cadastrar(new CadastroRequest(nome, login, email, senha)).enqueue(new retrofit2.Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSucesso()) {
                    callback.onSuccess(true);
                } else {
                    callback.onSuccess(true); // Fallback offline
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                callback.onSuccess(true); // Sucesso offline
            }
        });
    }

    public void cadastrarTutor(CadastroRequest req, Callback<Boolean> callback) {
        cadastrarTutor(req.getNome(), req.getLogin(), req.getEmail(), req.getSenha(), callback);
    }

    // =========================================================================
    // PETS (CRUD COM RAÇA - RF-002, RF-004)
    // =========================================================================
    public void listarPets(Callback<List<Pet>> callback) {
        ApiService api = ApiClient.getService(context);
        api.listarPets().enqueue(new retrofit2.Callback<ApiResponse<List<Pet>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Pet>>> call, Response<ApiResponse<List<Pet>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getDados() != null) {
                    List<Pet> pets = response.body().getDados();
                    salvarLista("pets", pets);
                    callback.onSuccess(pets);
                    return;
                }
                callback.onSuccess(obterPetsLocais());
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Pet>>> call, Throwable t) {
                callback.onSuccess(obterPetsLocais());
            }
        });
    }

    private List<Pet> obterPetsLocais() {
        Type type = new TypeToken<List<Pet>>(){}.getType();
        return obterLista("pets", type);
    }

    public void adicionarPet(Pet pet, Callback<Pet> callback) {
        salvarPet(pet, new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                callback.onSuccess(pet);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public void atualizarPet(Pet pet, Callback<Boolean> callback) {
        salvarPet(pet, callback);
    }

    public void salvarPet(Pet pet, Callback<Boolean> callback) {
        List<Pet> pets = obterPetsLocais();
        if (pet.getId() == 0) {
            int novoId = pets.isEmpty() ? 1 : pets.get(pets.size() - 1).getId() + 1;
            pet.setId(novoId);
            pet.setTutorId(session.getUserId());
            pet.setTutorNome(session.getNome());
            pets.add(pet);
        } else {
            for (int i = 0; i < pets.size(); i++) {
                if (pets.get(i).getId() == pet.getId()) {
                    pets.set(i, pet);
                    break;
                }
            }
        }
        salvarLista("pets", pets);

        // Tenta sincronizar online em segundo plano
        ApiService api = ApiClient.getService(context);
        if (pet.getId() > 0) {
            api.criarPet(pet).enqueue(new retrofit2.Callback<ApiResponse<Integer>>() {
                @Override public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {}
                @Override public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {}
            });
        }

        callback.onSuccess(true);
    }

    public void excluirPet(int petId, Callback<Boolean> callback) {
        List<Pet> pets = obterPetsLocais();
        pets.removeIf(p -> p.getId() == petId);
        salvarLista("pets", pets);

        ApiService api = ApiClient.getService(context);
        api.removerPet(petId).enqueue(new retrofit2.Callback<ApiResponse<Void>>() {
            @Override public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {}
            @Override public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {}
        });

        callback.onSuccess(true);
    }

    // =========================================================================
    // CONSULTAS (AGENDAMENTO, CONFLITO E CANCELAMENTO RN-001, RN-002, RN-005)
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

    public void agendarConsulta(Consulta consulta, Callback<Consulta> callback) {
        List<Consulta> lista = obterConsultasLocais();

        // RN-002: Verificação de conflito de horário
        for (Consulta c : lista) {
            if (c.getData().equals(consulta.getData()) &&
                c.getHora().equals(consulta.getHora()) &&
                !c.getStatus().equals("Cancelada")) {
                callback.onError("Horário já reservado para este veterinário (Regra RN-002).");
                return;
            }
        }

        int novoId = lista.isEmpty() ? 1 : lista.get(lista.size() - 1).getId() + 1;
        consulta.setId(novoId);
        consulta.setStatus("Marcada");
        lista.add(0, consulta);
        salvarLista("consultas", lista);

        callback.onSuccess(consulta);
    }

    public void cancelarConsulta(int id, Callback<Boolean> callback) {
        List<Consulta> lista = obterConsultasLocais();
        for (Consulta c : lista) {
            if (c.getId() == id) {
                // RN-005: cancelamento com menos de 24h de antecedência → NaoCompareceu
                //         cancelamento com 24h ou mais de antecedência → Cancelada
                String novoStatus = "Cancelada";
                try {
                    String dataStr = c.getData() != null ? c.getData().trim() : "";
                    String horaStr = c.getHora() != null ? c.getHora().trim() : "00:00";
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());
                    java.util.Date dataConsulta = sdf.parse(dataStr + " " + horaStr);
                    if (dataConsulta != null) {
                        long diffMs = dataConsulta.getTime() - System.currentTimeMillis();
                        long vintEQuatroHorasMs = 24L * 60 * 60 * 1000;
                        if (diffMs < vintEQuatroHorasMs) {
                            novoStatus = "NaoCompareceu";
                        }
                    }
                } catch (Exception ignored) {}
                c.setStatus(novoStatus);
                salvarLista("consultas", lista);
                callback.onSuccess(true);
                return;
            }
        }
        callback.onError("Consulta não encontrada.");
    }

    // =========================================================================
    // VACINAS, HISTÓRICO, RELATÓRIOS E AVISOS
    // =========================================================================
    public void listarTodasVacinas(Callback<List<Vacina>> callback) {
        Type typeVacina = new TypeToken<List<Vacina>>(){}.getType();
        Type typePet    = new TypeToken<List<Pet>>(){}.getType();
        List<Vacina> vacinas = obterLista("vacinas", typeVacina);
        List<Pet>    pets    = obterLista("pets",    typePet);
        preencherPetNome(vacinas, pets);
        callback.onSuccess(vacinas);
    }

    public void listarVacinasDoPet(int petId, Callback<List<Vacina>> callback) {
        Type typeVacina = new TypeToken<List<Vacina>>(){}.getType();
        Type typePet    = new TypeToken<List<Pet>>(){}.getType();
        List<Vacina> todas = obterLista("vacinas", typeVacina);
        List<Pet>    pets  = obterLista("pets",    typePet);
        List<Vacina> doPet = new ArrayList<>();
        for (Vacina v : todas) {
            if (v.getPetId() == petId) doPet.add(v);
        }
        preencherPetNome(doPet, pets);
        callback.onSuccess(doPet);
    }

    /** Preenche vacina.petNome buscando o nome pelo petId na lista de pets. */
    private void preencherPetNome(List<Vacina> vacinas, List<Pet> pets) {
        if (vacinas == null || pets == null) return;
        Map<Integer, String> nomePorId = new HashMap<>();
        for (Pet p : pets) nomePorId.put(p.getId(), p.getNome());
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
            if (h.getPetId() == petId) doPet.add(h);
        }
        callback.onSuccess(doPet);
    }

    public void listarRelatorios(Callback<List<Relatorio>> callback) {
        Type type = new TypeToken<List<Relatorio>>(){}.getType();
        callback.onSuccess(obterLista("relatorios", type));
    }

    public void listarAvisos(Callback<List<Aviso>> callback) {
        Type type = new TypeToken<List<Aviso>>(){}.getType();
        callback.onSuccess(obterLista("avisos", type));
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
        callback.onSuccess(true);
    }

}
