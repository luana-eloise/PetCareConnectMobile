package com.unip.petcareconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.LoginResponse;
import com.unip.petcareconnect.repository.PetCareRepository;
import com.unip.petcareconnect.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etSenha;
    private Button btnLogin, btnIrCadastro;
    private ProgressBar progressBar;
    private PetCareRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            abrirTelaPorPerfil(sessionManager.getPerfil());
            return;
        }

        setContentView(R.layout.activity_login);

        repository = new PetCareRepository(this);

        etEmail = findViewById(R.id.etLogin);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrCadastro = findViewById(R.id.btnCadastrar);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> tentarLogin());
        btnIrCadastro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class)));
    }

    private void tentarLogin() {
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Informe seu e-mail ou usuário");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(senha)) {
            etSenha.setError("Informe sua senha");
            etSenha.requestFocus();
            return;
        }

        setLoading(true);

        repository.login(email, senha, new PetCareRepository.RepositoryCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                setLoading(false);
                if (response != null && response.getToken() != null) {
                    // O repositório já salvou a sessão (token, id, nome, login, perfil)
                    Toast.makeText(LoginActivity.this,
                            "Bem-vindo, " + response.getNome() + "!", Toast.LENGTH_SHORT).show();
                    abrirTelaPorPerfil(response.getPerfil());
                } else {
                    Snackbar.make(btnLogin, "Erro ao processar login.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String mensagem) {
                setLoading(false);
                Snackbar.make(btnLogin, "Falha no login: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        if (progressBar != null) progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnIrCadastro.setEnabled(!loading);
    }

    private void abrirTelaPorPerfil(String perfil) {
        Intent intent;
        if ("Veterinario".equalsIgnoreCase(perfil)) {
            intent = new Intent(this, VetMainActivity.class);
        } else if ("Admin".equalsIgnoreCase(perfil)) {
            intent = new Intent(this, AdminMainActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarDialogConfiguracaoUrl() {
        final EditText input = new EditText(this);
        input.setText(sessionManager.getBaseUrl());
        input.setHint("http://10.0.2.2:5000/");

        new AlertDialog.Builder(this)
                .setTitle("Configurar URL da API (ASP.NET)")
                .setMessage("Insira o endpoint base do backend PetCare Connect:")
                .setView(input)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String novaUrl = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(novaUrl)) {
                        sessionManager.setBaseUrl(novaUrl);
                        Toast.makeText(LoginActivity.this, "URL atualizada!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}