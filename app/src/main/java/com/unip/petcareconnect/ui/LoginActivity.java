package com.unip.petcareconnect.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.LoginResponse;
import com.unip.petcareconnect.repository.PetCareRepository;
import com.unip.petcareconnect.util.SessionManager;

/**
 * Tela de autenticação exclusiva para Tutores.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etLogin;
    private EditText etSenha;
    private Button btnLogin;
    private Button btnIrCadastro;
    private TextView tvEsqueciSenha;
    private ProgressBar progressBar;

    private PetCareRepository repository;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            abrirMain();
            return;
        }

        setContentView(R.layout.activity_login);

        repository = new PetCareRepository(this);

        etLogin = findViewById(R.id.etLogin);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrCadastro = findViewById(R.id.btnCadastrar);
        tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> tentarLogin());
        btnIrCadastro.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));
        tvEsqueciSenha.setOnClickListener(v -> mostrarDialogRecuperacaoSenha());
    }

    private void tentarLogin() {
        String login = etLogin.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (TextUtils.isEmpty(login)) {
            etLogin.setError("Informe seu e-mail ou usuário");
            etLogin.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(senha)) {
            etSenha.setError("Informe sua senha");
            etSenha.requestFocus();
            return;
        }

        setCarregando(true);

        repository.login(login, senha, new PetCareRepository.RepositoryCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                setCarregando(false);
                if (response != null && response.getToken() != null) {
                    abrirMain();
                } else {
                    Snackbar.make(btnLogin, "Erro ao processar o login.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String mensagem) {
                setCarregando(false);
                if (mensagem != null && mensagem.startsWith(PetCareRepository.PREFIXO_ERRO_PERFIL)) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Acesso restrito")
                            .setMessage(mensagem)
                            .setPositiveButton("Entendi", null)
                            .show();
                } else {
                    Snackbar.make(btnLogin, mensagem != null ? mensagem : "Erro desconhecido.", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void mostrarDialogRecuperacaoSenha() {
        final EditText input = new EditText(this);
        input.setHint("E-mail cadastrado");
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        new AlertDialog.Builder(this)
                .setTitle("Recuperar senha")
                .setMessage("Informe o e-mail da sua conta para receber as instruções de recuperação.")
                .setView(input)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String email = input.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        return;
                    }
                    // Simulação da solicitação de recuperação de senha enviada ao usuário
                    Snackbar.make(btnLogin,
                            "Se o e-mail estiver cadastrado, você receberá as instruções para redefinição.",
                            Snackbar.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setCarregando(boolean carregando) {
        progressBar.setVisibility(carregando ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!carregando);
        btnIrCadastro.setEnabled(!carregando);
    }

    private void abrirMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
