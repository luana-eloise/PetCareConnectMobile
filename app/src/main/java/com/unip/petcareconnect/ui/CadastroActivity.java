package com.unip.petcareconnect.ui;

import android.content.Intent;
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
 * Cadastro de Tutor. Não há campo de login na tela: o e-mail é enviado também como login
 * (a API limita o login a 60 caracteres). As regras finais são do servidor.
 */
public class CadastroActivity extends AppCompatActivity {

    /** Devolvido ao Login para preencher o campo de usuário. */
    public static final String EXTRA_EMAIL = "email_cadastrado";

    private static final int MAX_NOME = 120;
    private static final int MAX_EMAIL_LOGIN = 60;
    private static final int MIN_SENHA = 6;
    private static final int MAX_SENHA = 72;

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
        // O login também corta espaços nas pontas da senha; os dois ficam consistentes.
        String senha = etSenha.getText().toString().trim();

        if (TextUtils.isEmpty(nome)) {
            etNome.setError("Informe seu nome completo");
            etNome.requestFocus();
            return;
        }
        if (nome.length() > MAX_NOME) {
            etNome.setError("O nome deve ter no máximo " + MAX_NOME + " caracteres");
            etNome.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Informe um e-mail válido");
            etEmail.requestFocus();
            return;
        }
        if (email.length() > MAX_EMAIL_LOGIN) {
            etEmail.setError("O e-mail deve ter no máximo " + MAX_EMAIL_LOGIN + " caracteres");
            etEmail.requestFocus();
            return;
        }
        if (senha.length() < MIN_SENHA || senha.length() > MAX_SENHA) {
            etSenha.setError("A senha deve ter entre " + MIN_SENHA + " e " + MAX_SENHA + " caracteres");
            etSenha.requestFocus();
            return;
        }

        final String emailFinal = email.toLowerCase();
        CadastroRequest req = new CadastroRequest(nome, emailFinal, emailFinal, senha);

        setCarregando(true);
        repository.cadastrarTutor(req, new PetCareRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
                Toast.makeText(CadastroActivity.this,
                        "Cadastro realizado com sucesso! Faça login para continuar.",
                        Toast.LENGTH_LONG).show();
                Intent resultado = new Intent();
                resultado.putExtra(EXTRA_EMAIL, emailFinal);
                setResult(RESULT_OK, resultado);
                finish();
            }

            @Override
            public void onError(String mensagem) {
                setCarregando(false);
                Snackbar.make(btnCadastrar, traduzirMensagem(mensagem), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /** O usuário não vê campo de login, então "login já em uso" vira "e-mail já cadastrado". */
    private String traduzirMensagem(String mensagem) {
        if (mensagem == null || mensagem.isEmpty()) return "Não foi possível concluir o cadastro.";
        if (mensagem.toLowerCase().contains("login já está em uso")) {
            return "Este e-mail já está cadastrado.";
        }
        return mensagem;
    }

    private void setCarregando(boolean carregando) {
        btnCadastrar.setEnabled(!carregando);
        btnVoltarLogin.setEnabled(!carregando);
        btnCadastrar.setText(carregando ? "Cadastrando..." : "Cadastrar");
    }
}