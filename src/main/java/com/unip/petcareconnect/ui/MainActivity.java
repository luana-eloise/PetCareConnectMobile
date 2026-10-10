package com.unip.petcareconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.Aviso;
import com.unip.petcareconnect.model.Consulta;
import com.unip.petcareconnect.model.Pet;
import com.unip.petcareconnect.model.Relatorio;
import com.unip.petcareconnect.model.Vacina;
import com.unip.petcareconnect.repository.PetCareRepository;
import com.unip.petcareconnect.util.SessionManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserRole;
    private TextView tvStatPets, tvStatConsultas, tvStatVacinas, tvStatRelatorios;
    private CardView cardPets, cardConsultas, cardVacinas, cardRelatorios, cardAvisos;
    private ImageButton btnLogout;

    private SessionManager sessionManager;
    private PetCareRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            fazerLogout();
            return;
        }

        setContentView(R.layout.activity_main);
        repository = new PetCareRepository(this);

        tvUserName = findViewById(R.id.tvMainGreeting);
        tvUserRole = findViewById(R.id.tvMainPerfil);
        tvStatPets = findViewById(R.id.tvStatPets);
        tvStatConsultas = findViewById(R.id.tvStatConsultas);
        tvStatVacinas = findViewById(R.id.tvStatVacinas);
        tvStatRelatorios = findViewById(R.id.tvStatRelatorios);

        cardPets = findViewById(R.id.cardNavPets);
        cardConsultas = findViewById(R.id.cardNavConsultas);
        cardVacinas = findViewById(R.id.cardNavVacinas);
        cardRelatorios = findViewById(R.id.cardNavRelatorios);
        cardAvisos = findViewById(R.id.cardNavAvisos);
        btnLogout = findViewById(R.id.btnMainLogout);

        configurarUsuario();
        configurarNavegacao();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEstatisticas();
    }

    private void configurarUsuario() {
        String nome = sessionManager.getNome();
        String role = sessionManager.getPerfil();

        tvUserName.setText(nome != null && !nome.isEmpty() ? nome : "Usuário");
        tvUserRole.setText("Perfil: " + (role != null && !role.isEmpty() ? role : "Tutor"));

        btnLogout.setOnClickListener(v -> confirmarLogout());
    }

    private void configurarNavegacao() {
        cardPets.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PetsActivity.class)));
        cardConsultas.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ConsultasActivity.class)));
        cardVacinas.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, VacinasActivity.class)));
        cardRelatorios.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RelatoriosActivity.class)));
        cardAvisos.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AvisosActivity.class)));
    }

    private void carregarEstatisticas() {
        // Carrega Pets
        repository.listarPets(new PetCareRepository.RepositoryCallback<List<Pet>>() {
            @Override
            public void onSuccess(List<Pet> pets) {
                int totalPets = (pets != null) ? pets.size() : 0;
                tvStatPets.setText(String.valueOf(totalPets));
            }

            @Override
            public void onError(String mensagem) {
                // mantém valor prévio
            }
        });

        // Carrega Consultas (apenas marcadas a serem realizadas)
        repository.listarConsultas(new PetCareRepository.RepositoryCallback<List<Consulta>>() {
            @Override
            public void onSuccess(List<Consulta> consultas) {
                int totalMarcadas = 0;
                if (consultas != null) {
                    for (Consulta c : consultas) {
                        if (c != null && c.getStatus() != null) {
                            String status = c.getStatus().trim();
                            if ("Marcada".equalsIgnoreCase(status) || "Agendada".equalsIgnoreCase(status) || "Confirmada".equalsIgnoreCase(status)) {
                                totalMarcadas++;
                            }
                        }
                    }
                }
                tvStatConsultas.setText(String.valueOf(totalMarcadas));
            }

            @Override
            public void onError(String mensagem) {
            }
        });

        // Carrega Vacinas (apenas pendentes)
        repository.listarTodasVacinas(new PetCareRepository.RepositoryCallback<List<Vacina>>() {
            @Override
            public void onSuccess(List<Vacina> vacinas) {
                int pendentes = 0;
                if (vacinas != null) {
                    for (Vacina v : vacinas) {
                        if (v != null && !v.isAplicada()) {
                            pendentes++;
                        }
                    }
                }
                tvStatVacinas.setText(String.valueOf(pendentes));
            }

            @Override
            public void onError(String mensagem) {
            }
        });

        // Carrega Relatórios
        repository.listarRelatorios(new PetCareRepository.RepositoryCallback<List<Relatorio>>() {
            @Override
            public void onSuccess(List<Relatorio> relatorios) {
                int laudos = (relatorios != null) ? relatorios.size() : 0;
                tvStatRelatorios.setText(String.valueOf(laudos));
            }

            @Override
            public void onError(String mensagem) {
            }
        });
    }

    private void confirmarLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Sair do PetCare Connect")
                .setMessage("Deseja realmente desconectar da sua conta?")
                .setPositiveButton("Sim, sair", (dialog, which) -> fazerLogout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void fazerLogout() {
        sessionManager.logout();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
