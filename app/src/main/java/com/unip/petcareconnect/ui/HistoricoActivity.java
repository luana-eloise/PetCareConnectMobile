package com.unip.petcareconnect.ui;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.adapter.HistoricoAdapter;
import com.unip.petcareconnect.model.Historico;
import com.unip.petcareconnect.repository.PetCareRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Histórico Clínico de um Pet específico.
 * Recebe {@link #EXTRA_PET_ID} e {@link #EXTRA_PET_NOME} via Intent.
 */
public class HistoricoActivity extends AppCompatActivity {

    public static final String EXTRA_PET_ID   = "pet_id";
    public static final String EXTRA_PET_NOME = "pet_nome";

    private RecyclerView rvHistorico;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private PetCareRepository repository;
    private final List<Historico> historicoList = new ArrayList<>();
    private HistoricoAdapter adapter;
    private int petId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        petId = getIntent().getIntExtra(EXTRA_PET_ID, -1);
        String petNome = getIntent().getStringExtra(EXTRA_PET_NOME);

        repository = new PetCareRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        rvHistorico  = findViewById(R.id.rvHistorico);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar  = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Histórico: " + (petNome != null ? petNome : "Pet"));
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new HistoricoAdapter(historicoList);
        rvHistorico.setLayoutManager(new LinearLayoutManager(this));
        rvHistorico.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::carregarHistorico);
        carregarHistorico();
    }

    private void carregarHistorico() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        repository.listarHistorico(petId, new PetCareRepository.RepositoryCallback<List<Historico>>() {
            @Override
            public void onSuccess(List<Historico> itens) {
                progressBar.setVisibility(android.view.View.GONE);
                swipeRefresh.setRefreshing(false);
                historicoList.clear();
                if (itens != null && !itens.isEmpty()) {
                    historicoList.addAll(itens);
                    tvEmptyState.setVisibility(android.view.View.GONE);
                } else {
                    tvEmptyState.setVisibility(android.view.View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensagem) {
                progressBar.setVisibility(android.view.View.GONE);
                swipeRefresh.setRefreshing(false);
                Snackbar.make(rvHistorico, "Erro ao carregar histórico: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
