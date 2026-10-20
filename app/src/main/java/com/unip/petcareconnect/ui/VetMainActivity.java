package com.unip.petcareconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Consulta;
import com.unip.petcareconnect.model.Relatorio;
import com.unip.petcareconnect.repository.PetCareRepository;
import com.unip.petcareconnect.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class VetMainActivity extends AppCompatActivity {

    private SessionManager session;
    private PetCareRepository repository;

    private TextView tvStatMarcadas, tvStatRealizadas, tvStatPacientes, tvStatRelatorios, tvVazio;
    private LinearLayout containerConsultas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (!session.isLoggedIn() || !"Veterinario".equalsIgnoreCase(session.getPerfil())) {
            // O LoginActivity redireciona para a tela correta do perfil
            irParaLogin();
            return;
        }

        setContentView(R.layout.activity_vet_main);
        repository = new PetCareRepository(this);

        TextView tvGreeting = findViewById(R.id.tvVetGreeting);
        tvStatMarcadas = findViewById(R.id.tvVetStatMarcadas);
        tvStatRealizadas = findViewById(R.id.tvVetStatRealizadas);
        tvStatPacientes = findViewById(R.id.tvVetStatPacientes);
        tvStatRelatorios = findViewById(R.id.tvVetStatRelatorios);
        tvVazio = findViewById(R.id.tvVetVazio);
        containerConsultas = findViewById(R.id.containerConsultasVet);
        ImageButton btnLogout = findViewById(R.id.btnVetLogout);

        String nome = session.getNome();
        tvGreeting.setText("Olá, " + (nome != null && !nome.isEmpty() ? nome : "Veterinário") + " 👋");
        btnLogout.setOnClickListener(v -> confirmarLogout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (repository != null) carregar();
    }

    private void carregar() {
        repository.listarConsultasDoPerfil(new PetCareRepository.RepositoryCallback<List<Consulta>>() {
            @Override
            public void onSuccess(List<Consulta> consultas) {
                mostrar(consultas);
            }

            @Override
            public void onError(String mensagem) {
                Toast.makeText(VetMainActivity.this, "Erro ao carregar: " + mensagem, Toast.LENGTH_SHORT).show();
            }
        });

        repository.listarRelatorios(new PetCareRepository.RepositoryCallback<List<Relatorio>>() {
            @Override
            public void onSuccess(List<Relatorio> relatorios) {
                tvStatRelatorios.setText(String.valueOf(relatorios != null ? relatorios.size() : 0));
            }

            @Override
            public void onError(String mensagem) { }
        });
    }

    private void mostrar(List<Consulta> consultas) {
        List<Consulta> lista = consultas != null ? new ArrayList<>(consultas) : new ArrayList<>();

        int marcadas = 0, realizadas = 0;
        Set<Integer> pacientes = new HashSet<>();
        for (Consulta c : lista) {
            if (isMarcada(c)) marcadas++;
            if (c.getStatus() != null && "Realizada".equalsIgnoreCase(c.getStatus().trim())) realizadas++;
            pacientes.add(c.getPetId());
        }

        tvStatMarcadas.setText(String.valueOf(marcadas));
        tvStatRealizadas.setText(String.valueOf(realizadas));
        tvStatPacientes.setText(String.valueOf(pacientes.size()));

        // Marcadas primeiro, depois por data/hora
        Collections.sort(lista, (a, b) -> {
            boolean am = isMarcada(a), bm = isMarcada(b);
            if (am != bm) return am ? -1 : 1;
            return Long.compare(timestamp(a), timestamp(b));
        });

        containerConsultas.removeAllViews();
        tvVazio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);

        LayoutInflater inflater = getLayoutInflater();
        for (Consulta c : lista) {
            View item = inflater.inflate(R.layout.item_consulta_dashboard, containerConsultas, false);

            TextView tvPet = item.findViewById(R.id.tvItemPet);
            TextView tvDataHora = item.findViewById(R.id.tvItemDataHora);
            TextView tvDetalhe = item.findViewById(R.id.tvItemDetalhe);
            TextView tvStatus = item.findViewById(R.id.tvItemStatus);
            Button btnConcluir = item.findViewById(R.id.btnItemConcluir);

            tvPet.setText("🐾 " + valor(c.getPetNome(), "Pet #" + c.getPetId()));
            tvDataHora.setText("📅 " + valor(c.getData(), "--/--/----") + " às " + valor(c.getHora(), "--:--"));
            String obs = c.getObservacoes();
            tvDetalhe.setText(obs != null && !obs.isEmpty() ? obs : "Sem observações");
            tvDetalhe.setVisibility(View.VISIBLE);

            String status = valor(c.getStatus(), "Marcada");
            tvStatus.setText("Status: " + status);
            boolean ruim = "Cancelada".equalsIgnoreCase(status) || "NaoCompareceu".equalsIgnoreCase(status);
            tvStatus.setTextColor(ContextCompat.getColor(this, ruim ? R.color.status_red : R.color.teal_primary));

            if (isMarcada(c)) {
                btnConcluir.setVisibility(View.VISIBLE);
                btnConcluir.setOnClickListener(v -> concluir(c));
            }

            containerConsultas.addView(item);
        }
    }

    private void concluir(Consulta c) {
        new AlertDialog.Builder(this)
                .setTitle("Concluir consulta")
                .setMessage("Marcar a consulta de " + valor(c.getPetNome(), "este pet") + " como realizada?")
                .setPositiveButton("Sim", (d, w) ->
                        repository.atualizarStatusConsulta(c.getId(), "Realizada",
                                new PetCareRepository.RepositoryCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean ok) {
                                        Toast.makeText(VetMainActivity.this, "Consulta concluída!", Toast.LENGTH_SHORT).show();
                                        carregar();
                                    }

                                    @Override
                                    public void onError(String mensagem) {
                                        Toast.makeText(VetMainActivity.this, mensagem, Toast.LENGTH_SHORT).show();
                                    }
                                }))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private boolean isMarcada(Consulta c) {
        if (c == null || c.getStatus() == null) return false;
        String s = c.getStatus().trim();
        return "Marcada".equalsIgnoreCase(s) || "Agendada".equalsIgnoreCase(s) || "Confirmada".equalsIgnoreCase(s);
    }

    private long timestamp(Consulta c) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.parse(c.getData().trim() + " " + c.getHora().trim()).getTime();
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private String valor(String v, String padrao) {
        return (v != null && !v.trim().isEmpty()) ? v : padrao;
    }

    private void confirmarLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair do PetCare Connect")
                .setMessage("Deseja realmente desconectar da sua conta?")
                .setPositiveButton("Sim, sair", (d, w) -> {
                    session.logout();
                    irParaLogin();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void irParaLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}