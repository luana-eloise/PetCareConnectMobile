package com.unip.petcareconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

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

/**
 * Tela principal do Tutor.
 * Exibe estatísticas rápidas e navegação para as seções do app.
 */
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

        tvUserName      = findViewById(R.id.tvMainGreeting);
        tvUserRole      = findViewById(R.id.tvMainPerfil);
        tvStatPets      = findViewById(R.id.tvStatPets);
        tvStatConsultas = findViewById(R.id.tvStatConsultas);
        tvStatVacinas   = findViewById(R.id.tvStatVacinas);
        tvStatRelatorios = findViewById(R.id.tvStatRelatorios);

        cardPets       = findViewById(R.id.cardNavPets);
        cardConsultas  = findViewById(R.id.cardNavConsultas);
        cardVacinas    = findViewById(R.id.cardNavVacinas);
        cardRelatorios = findViewById(R.id.cardNavRelatorios);
        cardAvisos     = findViewById(R.id.cardNavAvisos);
        btnLogout      = findViewById(R.id.btnMainLogout);

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
        // SessionManager.getPerfil() sempre retorna "Tutor" — exibição simplificada
        tvUserName.setText(nome != null && !nome.isEmpty() ? nome : "Usuário");
        tvUserRole.setText("Perfil: Tutor");
        btnLogout.setOnClickListener(v -> confirmarLogout());
    }

    private void configurarNavegacao() {
        cardPets.setOnClickListener(v -> startActivity(new Intent(this, PetsActivity.class)));
        cardConsultas.setOnClickListener(v -> startActivity(new Intent(this, ConsultasActivity.class)));
        cardVacinas.setOnClickListener(v -> startActivity(new Intent(this, VacinasActivity.class)));
        cardRelatorios.setOnClickListener(v -> startActivity(new Intent(this, RelatoriosActivity.class)));
        cardAvisos.setOnClickListener(v -> startActivity(new Intent(this, AvisosActivity.class)));
    }

    private void carregarEstatisticas() {
        carregarTotalPets();
        carregarConsultasMarcadas();
        carregarVacinasPendentes();
        carregarTotalRelatorios();
    }

    private void carregarTotalPets() {
        repository.listarPets(new PetCareRepository.RepositoryCallback<List<Pet>>() {
            @Override
            public void onSuccess(List<Pet> pets) {
                tvStatPets.setText(String.valueOf(pets != null ? pets.size() : 0));
            }

            @Override
            public void onError(String mensagem) { /* mantém valor prévio */ }
        });
    }

    private void carregarConsultasMarcadas() {
        repository.listarConsultas(new PetCareRepository.RepositoryCallback<List<Consulta>>() {
            @Override
            public void onSuccess(List<Consulta> consultas) {
                int totalMarcadas = 0;
                if (consultas != null) {
                    for (Consulta c : consultas) {
                        if (c != null && c.isMarcada()) {
                            totalMarcadas++;
                        }
                    }
                }
                tvStatConsultas.setText(String.valueOf(totalMarcadas));
            }

            @Override
            public void onError(String mensagem) { /* mantém valor prévio */ }
        });
    }

    private void carregarVacinasPendentes() {
        repository.listarTodasVacinas(new PetCareRepository.RepositoryCallback<List<Vacina>>() {
            @Override
            public void onSuccess(List<Vacina> vacinas) {
                int pendentes = 0;
                if (vacinas != null) {
                    for (Vacina v : vacinas) {
                        if (v != null && !v.isAplicada()) pendentes++;
                    }
                }
                tvStatVacinas.setText(String.valueOf(pendentes));
            }

            @Override
            public void onError(String mensagem) { /* mantém valor prévio */ }
        });
    }

    private void carregarTotalRelatorios() {
        repository.listarRelatorios(new PetCareRepository.RepositoryCallback<List<Relatorio>>() {
            @Override
            public void onSuccess(List<Relatorio> relatorios) {
                tvStatRelatorios.setText(String.valueOf(relatorios != null ? relatorios.size() : 0));
            }

            @Override
            public void onError(String mensagem) { /* mantém valor prévio */ }
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
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
