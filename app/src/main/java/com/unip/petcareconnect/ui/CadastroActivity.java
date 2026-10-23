package com.unip.petcareconnect.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.unip.petcareconnect.R;
import com.unip.petcareconnect.model.CadastroRequest;
import com.unip.petcareconnect.repository.PetCareRepository;

/**
 * Tela de cadastro exclusiva para Tutores.
 * Utiliza o e-mail como login (não há campo de login separado no layout).
 */
public class CadastroActivity extends AppCompatActivity {

    private EditText etNome, etEmail, etSenha;
    private Button btnCadastrar, btnVoltarLogin;
    private PetCareRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        repository = new PetCareRepository(this);

        etNome         = findViewById(R.id.etCadNome);
        etEmail        = findViewById(R.id.etCadEmail);
        etSenha        = findViewById(R.id.etCadSenha);
        btnCadastrar   = findViewById(R.id.btnCadConfirmar);
        btnVoltarLogin = findViewById(R.id.btnCadVoltar);

        btnCadastrar.setOnClickListener(v -> tentarCadastro());
        btnVoltarLogin.setOnClickListener(v -> finish());
    }

    private void tentarCadastro() {
        String nome  = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String senha = etSenha.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            etNome.setError("Informe seu nome completo");
            etNome.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Informe um e-mail válido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(senha) || senha.length() < 6) {
            etSenha.setError("A senha deve ter no mínimo 6 caracteres");
            etSenha.requestFocus();
            return;
        }

        // O e-mail é usado como login (não há campo separado no layout)
        CadastroRequest req = new CadastroRequest(nome, email, email, senha);

        repository.cadastrarTutor(req, new PetCareRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Toast.makeText(CadastroActivity.this,
                        "Cadastro realizado com sucesso! Faça login para continuar.",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String mensagem) {
                Snackbar.make(btnCadastrar, "Erro ao cadastrar: " + mensagem, Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
