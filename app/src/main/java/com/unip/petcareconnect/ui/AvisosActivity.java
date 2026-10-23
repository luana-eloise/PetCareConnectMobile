package com.unip.petcareconnect.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.adapter.AvisoAdapter;
import com.unip.petcareconnect.model.Aviso;
import com.unip.petcareconnect.repository.PetCareRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Tela de Avisos e Lembretes do Tutor.
 * Exibe notificações da clínica e permite marcar como lido/não lido.
 */
public class AvisosActivity extends AppCompatActivity {

    private RecyclerView rvAvisos;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private AvisoAdapter adapter;
    private PetCareRepository repository;
    private final List<Aviso> avisoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avisos);

        repository = new PetCareRepository(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        rvAvisos     = findViewById(R.id.rvAvisos);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar  = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Avisos e Lembretes");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvAvisos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AvisoAdapter(avisoList, new AvisoAdapter.OnAvisoActionListener() {
            @Override
            public void onToggleLido(Aviso aviso) { alternarStatusLido(aviso); }

            @Override
            public void onAvisoClick(Aviso aviso) { mostrarDetalhesAviso(aviso); }
        });
        rvAvisos.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::carregarAvisos);
        carregarAvisos();
    }

    private void carregarAvisos() {
        progressBar.setVisibility(View.VISIBLE);
        repository.listarAvisos(new PetCareRepository.Callback<List<Aviso>>() {
            @Override
            public void onSuccess(List<Aviso> avisos) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                avisoList.clear();
                if (avisos != null && !avisos.isEmpty()) {
                    avisoList.addAll(avisos);
                    tvEmptyState.setVisibility(View.GONE);
                } else {
                    tvEmptyState.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String mensagem) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Snackbar.make(rvAvisos, "Erro ao carregar avisos: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void alternarStatusLido(Aviso aviso) {
        boolean novoStatus = !aviso.isLido();
        aviso.setLido(novoStatus);
        adapter.notifyDataSetChanged();

        repository.atualizarStatusAviso(aviso.getId(), novoStatus, new PetCareRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                String msg = novoStatus ? "Aviso marcado como lido." : "Aviso marcado como não lido.";
                Toast.makeText(AvisosActivity.this, msg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String message) { /* falha silenciosa — UI já foi atualizada */ }
        });
    }

    private void mostrarDetalhesAviso(Aviso aviso) {
        // Marca automaticamente como lido ao abrir detalhes
        if (!aviso.isLido()) {
            aviso.setLido(true);
            adapter.notifyDataSetChanged();
            repository.atualizarStatusAviso(aviso.getId(), true, new PetCareRepository.Callback<Boolean>() {
                @Override public void onSuccess(Boolean result) {}
                @Override public void onError(String message) {}
            });
        }

        new AlertDialog.Builder(this)
                .setTitle(aviso.getTitulo())
                .setMessage("Prioridade: " + aviso.getPrioridade() + "\n" +
                        "Data: " + aviso.getData() + "\n\n" +
                        aviso.getMensagem())
                .setPositiveButton("Fechar", null)
                .setNeutralButton("Marcar como não lido", (dialog, which) -> alternarStatusLido(aviso))
                .show();
    }
}
