package com.unip.petcareconnect.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Gerenciador de Sessão e Preferências Locais do Tutor autenticado.
 * Encapsula o armazenamento seguro do token JWT, identificação do Tutor e configurações de rede.
 */
public class SessionManager {

    private static final String PREF_NAME = "PetCareConnectPref";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PERFIL = "perfil";
    private static final String KEY_IS_LOGGED = "is_logged_in";
    private static final String KEY_BASE_URL = "base_url";

    public static final String PERFIL_TUTOR = "Tutor";
    // 10.0.2.2 é o alias padrão do Android Emulator para o localhost da máquina hospedeira
    public static final String DEFAULT_BASE_URL = "http://10.0.2.2:5000/";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        this.pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Salva a sessão ativa do Tutor após login bem-sucedido.
     */
    public void salvarSessao(String token, int userId, String nome, String login) {
        salvarSessao(token, userId, nome, login, PERFIL_TUTOR);
    }

    /**
     * Sobrecarga compatível para persistência da sessão.
     */
    public void salvarSessao(String token, int userId, String nome, String login, String perfil) {
        pref.edit()
                .putBoolean(KEY_IS_LOGGED, true)
                .putString(KEY_TOKEN, token != null ? token.trim() : "")
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_NOME, nome != null ? nome.trim() : "")
                .putString(KEY_LOGIN, login != null ? login.trim().toLowerCase() : "")
                .putString(KEY_PERFIL, PERFIL_TUTOR)
                .apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED, false) && hasToken();
    }

    public boolean hasToken() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public String getNome() {
        return pref.getString(KEY_NOME, "");
    }

    public String getLogin() {
        return pref.getString(KEY_LOGIN, "");
    }

    public String getPerfil() {
        return PERFIL_TUTOR;
    }

    public String getBaseUrl() {
        return pref.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    public void setBaseUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            String sanitizedUrl = url.trim();
            if (!sanitizedUrl.endsWith("/")) {
                sanitizedUrl += "/";
            }
            pref.edit().putString(KEY_BASE_URL, sanitizedUrl).apply();
        }
    }

    /**
     * Encerra a sessão corrente do Tutor e limpa as credenciais locais.
     */
    public void logout() {
        String currentBaseUrl = getBaseUrl(); // Preserva a URL configurada do servidor
        pref.edit().clear().apply();
        setBaseUrl(currentBaseUrl);
    }
}
