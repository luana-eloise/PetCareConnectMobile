package com.unip.petcareconnect.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Sessão do Tutor autenticado (token JWT e identificação).
 */
public class SessionManager {

    private static final String PREF_NAME = "PetCareConnectPref";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PERFIL = "perfil";
    private static final String KEY_IS_LOGGED = "is_logged_in";

    public static final String PERFIL_TUTOR = "Tutor";

    // API em produção (Render). Precisa de https:// e da barra no final.
    public static final String DEFAULT_BASE_URL = "https://petcare-api-5e89.onrender.com/";

    private final SharedPreferences pref;

    public SessionManager(Context context) {
        this.pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void salvarSessao(String token, int userId, String nome, String login, String perfil) {
        pref.edit()
                .putBoolean(KEY_IS_LOGGED, true)
                .putString(KEY_TOKEN, token != null ? token.trim() : "")
                .putInt(KEY_USER_ID, userId)
                .putString(KEY_NOME, nome != null ? nome.trim() : "")
                .putString(KEY_LOGIN, login != null ? login.trim().toLowerCase() : "")
                .putString(KEY_PERFIL, perfil != null ? perfil.trim() : "")
                .apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED, false) && hasToken();
    }

    public boolean hasToken() {
        String token = getToken();
        return token != null && !token.trim().isEmpty();
    }

    public String getToken() { return pref.getString(KEY_TOKEN, ""); }
    public int getUserId() { return pref.getInt(KEY_USER_ID, 0); }
    public String getNome() { return pref.getString(KEY_NOME, ""); }
    public String getLogin() { return pref.getString(KEY_LOGIN, ""); }
    public String getPerfil() { return pref.getString(KEY_PERFIL, ""); }

    public String getBaseUrl() { return DEFAULT_BASE_URL; }

    /** Mantido só por compatibilidade. Não faz nada. */
    public void setBaseUrl(String url) { }

    public void logout() {
        pref.edit().clear().apply();
    }
}