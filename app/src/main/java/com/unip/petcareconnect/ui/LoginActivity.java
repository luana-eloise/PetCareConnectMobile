package com.unip.petcareconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    private TextView tvEsqueciSenha;
    private ProgressBar progressBar;
    private PetCareRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            if ("Tutor".equalsIgnoreCase(sessionManager.getPerfil())) {
                abrirMain();
                return;
            }
            // Sessão antiga de vet/admin (de versões anteriores do app)
            sessionManager.logout();
        }

        setContentView(R.layout.activity_login);

        repository = new PetCareRepository(this);

        etEmail = findViewById(R.id.etLogin);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrCadastro = findViewById(R.id.btnCadastrar);
        tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> tentarLogin());
        btnIrCadastro.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class)));
        tvEsqueciSenha.setOnClickListener(v -> mostrarEsqueciSenha());
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
                    Toast.makeText(LoginActivity.this,
                            "Bem-vindo, " + response.getNome() + "!", Toast.LENGTH_SHORT).show();
                    abrirMain();
                } else {
                    Snackbar.make(btnLogin, "Erro ao processar login.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String mensagem) {
                setLoading(false);
                if (mensagem != null && mensagem.startsWith(PetCareRepository.PREFIXO_ERRO_PERFIL)) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Acesso pelo portal web")
                            .setMessage(mensagem)
                            .setPositiveButton("Entendi", null)
                            .show();
                } else {
                    Snackbar.make(btnLogin, mensagem, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mostrarEsqueciSenha() {
        final EditText input = new EditText(this);
        input.setHint("E-mail cadastrado");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(this)
                .setTitle("Esqueci minha senha")
                .setMessage("Informe o e-mail da sua conta para receber as instruções de recuperação.")
                .setView(input)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(this, "Informe o e-mail.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    // TODO: chamar o endpoint de recuperação de senha do backend quando existir
                    Toast.makeText(this,
                            "Se o e-mail estiver cadastrado, você receberá as instruções.",
                            Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnIrCadastro.setEnabled(!loading);
    }

    private void abrirMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}