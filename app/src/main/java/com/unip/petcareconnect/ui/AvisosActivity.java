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
 * Tela de Avisos do Tutor.
 * Lista os avisos da API e permite marcar como lido (a API não tem "não lido").
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
            public void onToggleLido(Aviso aviso) { marcarComoLido(aviso, true); }

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

    /** Só atualiza a tela se a API confirmar. */
    private void marcarComoLido(Aviso aviso, boolean avisar) {
        if (aviso.isLido()) return;
        repository.marcarAvisoLido(aviso.getId(), new PetCareRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                aviso.setLido(true);
                adapter.notifyDataSetChanged();
                if (avisar) {
                    Toast.makeText(AvisosActivity.this, "Aviso marcado como lido.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AvisosActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDetalhesAviso(Aviso aviso) {
        marcarComoLido(aviso, false); // abrir o aviso o marca como lido

        new AlertDialog.Builder(this)
                .setTitle(aviso.getTitulo())
                .setMessage(aviso.getTexto())
                .setPositiveButton("Fechar", null)
                .show();
    }
}