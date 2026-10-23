package com.unip.petcareconnect.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.adapter.VacinaAdapter;
import com.unip.petcareconnect.model.Pet;
import com.unip.petcareconnect.model.Vacina;
import com.unip.petcareconnect.repository.PetCareRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela da Carteira de Vacinação.
 * Permite filtrar vacinas por pet e visualizar detalhes de cada registro.
 */
public class VacinasActivity extends AppCompatActivity {

    private Spinner spinnerPets;
    private RecyclerView rvVacinas;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private VacinaAdapter adapter;
    private PetCareRepository repository;
    private final List<Vacina> vacinaList = new ArrayList<>();
    private final List<Pet> petList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vacinas);

        repository = new PetCareRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        spinnerPets  = findViewById(R.id.spinnerPets);
        rvVacinas    = findViewById(R.id.rvVacinas);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar  = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Carteira de Vacinação");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvVacinas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VacinaAdapter(vacinaList, this::mostrarDetalhesVacina);
        rvVacinas.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::carregarPetsESelecionar);
        carregarPetsESelecionar();
    }

    private void carregarPetsESelecionar() {
        progressBar.setVisibility(View.VISIBLE);
        repository.listarPets(new PetCareRepository.RepositoryCallback<List<Pet>>() {
            @Override
            public void onSuccess(List<Pet> pets) {
                petList.clear();
                List<String> nomes = new ArrayList<>();
                nomes.add("Todos os Pets");

                if (pets != null) {
                    petList.addAll(pets);
                    for (Pet p : pets) nomes.add(p.getNome());
                }

                spinnerPets.setAdapter(new ArrayAdapter<>(VacinasActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, nomes));

                spinnerPets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            carregarTodasVacinas();
                        } else {
                            carregarVacinasDoPet(petList.get(position - 1).getId());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });

                carregarTodasVacinas();
            }

            @Override
            public void onError(String mensagem) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Snackbar.make(rvVacinas, "Erro: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void carregarTodasVacinas() {
        progressBar.setVisibility(View.VISIBLE);
        repository.listarTodasVacinas(new PetCareRepository.RepositoryCallback<List<Vacina>>() {
            @Override
            public void onSuccess(List<Vacina> vacinas) { atualizarLista(vacinas); }

            @Override
            public void onError(String mensagem) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Snackbar.make(rvVacinas, "Erro ao carregar vacinas: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void carregarVacinasDoPet(int petId) {
        progressBar.setVisibility(View.VISIBLE);
        repository.listarVacinasDoPet(petId, new PetCareRepository.RepositoryCallback<List<Vacina>>() {
            @Override
            public void onSuccess(List<Vacina> vacinas) { atualizarLista(vacinas); }

            @Override
            public void onError(String mensagem) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    private void atualizarLista(List<Vacina> vacinas) {
        progressBar.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(false);
        vacinaList.clear();
        if (vacinas != null && !vacinas.isEmpty()) {
            vacinaList.addAll(vacinas);
            tvEmptyState.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    private void mostrarDetalhesVacina(Vacina vacina) {
        String msg = "Vacina: " + vacina.getNome() + "\n" +
                "Pet: " + vacina.getPetNome() + "\n" +
                "Dose: " + vacina.getDose() + "\n" +
                "Lote: " + (vacina.getLote() != null ? vacina.getLote() : "N/I") + "\n" +
                "Data de Aplicação: " + vacina.getDataAplicacao() + "\n" +
                "Próxima Dose: " + (vacina.getProximaDose() != null ? vacina.getProximaDose() : "Não requer") + "\n" +
                "Veterinário: " + (vacina.getVeterinarioNome() != null ? vacina.getVeterinarioNome() : "Clínica PetCare");

        new AlertDialog.Builder(this)
                .setTitle("Registro de Vacinação")
                .setMessage(msg)
                .setPositiveButton("Fechar", null)
                .show();
    }
}
