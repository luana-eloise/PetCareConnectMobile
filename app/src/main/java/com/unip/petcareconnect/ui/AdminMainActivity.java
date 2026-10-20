package com.unip.petcareconnect.ui;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.unip.petcareconnect.R;
import com.unip.petcareconnect.util.SessionManager;

public class AdminMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (!"Admin".equalsIgnoreCase(session.getPerfil())) {
            finish();
            return;
        }

        setContentView(R.layout.activity_admin_main);

        // Usando o ID correto que está no seu XML novo (tvAdminGreeting)
        TextView tv = findViewById(R.id.tvAdminGreeting);
        tv.setText("Olá, " + session.getNome());

        // Botão de logout (caso queira programar a ação de sair depois)
        ImageButton btnLogout = findViewById(R.id.btnAdminLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                session.logout();
                finish();
            });
        }
    }
}