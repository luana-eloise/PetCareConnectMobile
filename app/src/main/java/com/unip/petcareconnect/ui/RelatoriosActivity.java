package com.unip.petcareconnect.ui;

import android.content.Intent;
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
import com.unip.petcareconnect.adapter.RelatorioAdapter;
import com.unip.petcareconnect.model.Relatorio;
import com.unip.petcareconnect.repository.PetCareRepository;

import java.util.ArrayList;
import java.util.List;

public class RelatoriosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView rvRelatorios;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    private RelatorioAdapter adapter;
    private PetCareRepository repository;
    private final List<Relatorio> relatorioList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);

        repository = new PetCareRepository(this);

        toolbar = findViewById(R.id.toolbar);
        rvRelatorios = findViewById(R.id.rvRelatorios);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Relatórios Clínicos");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvRelatorios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RelatorioAdapter(relatorioList, new RelatorioAdapter.OnRelatorioActionListener() {
            @Override
            public void onRelatorioClick(Relatorio relatorio) {
                mostrarDetalhes(relatorio);
            }

            @Override
            public void onExportarClick(Relatorio relatorio) {
                exportarRelatorio(relatorio);
            }
        });
        rvRelatorios.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::carregarRelatorios);

        carregarRelatorios();
    }

    private void carregarRelatorios() {
        progressBar.setVisibility(View.VISIBLE);
        repository.listarRelatorios(new PetCareRepository.Callback<List<Relatorio>>() {
            @Override
            public void onSuccess(List<Relatorio> relatorios) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                relatorioList.clear();
                if (relatorios != null && !relatorios.isEmpty()) {
                    relatorioList.addAll(relatorios);
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
                Snackbar.make(rvRelatorios, "Erro ao carregar relatórios: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void mostrarDetalhes(Relatorio relatorio) {
        String msg = "Tipo: " + relatorio.getTipo() + "\n" +
                "Pet: " + relatorio.getPetNome() + "\n" +
                "Data: " + relatorio.getDataEmissao() + "\n" +
                "Veterinário Responsável: " + (relatorio.getVeterinarioNome() != null ? relatorio.getVeterinarioNome() : "Clínica PetCare") + "\n\n" +
                "DIAGNÓSTICO:\n" + relatorio.getDiagnostico() + "\n\n" +
                "CONDUTA / PRESCRIÇÃO:\n" + (relatorio.getConduta() != null ? relatorio.getConduta() : "Nenhuma recomendação especial.");

        new AlertDialog.Builder(this)
                .setTitle(relatorio.getTitulo())
                .setMessage(msg)
                .setPositiveButton("Compartilhar", (dialog, which) -> exportarRelatorio(relatorio))
                .setNegativeButton("Fechar", null)
                .show();
    }

    private void exportarRelatorio(Relatorio relatorio) {
        String conteudo = "=== PETCARE CONNECT - LAUDO CLÍNICO SIMPLIFICADO===\n" +
                "Título: " + relatorio.getTitulo() + "\n" +
                "Tipo: " + relatorio.getTipo() + "\n" +
                "Pet: " + relatorio.getPetNome() + "\n" +
                "Data de Emissão: " + relatorio.getDataEmissao() + "\n" +
                "Veterinário: " + relatorio.getVeterinarioNome() + "\n" +
                "----------------------------------------\n" +
                "DIAGNÓSTICO:\n" + relatorio.getDiagnostico() + "\n\n" +
                "CONDUTA / TRATAMENTO:\n" + (relatorio.getConduta() != null ? relatorio.getConduta() : "Em acompanhamento") + "\n" +
                "========================================";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Relatório Clínico - " + relatorio.getPetNome());
        sendIntent.putExtra(Intent.EXTRA_TEXT, conteudo);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Exportar / Enviar Relatório");
        startActivity(shareIntent);
    }
}
