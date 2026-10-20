package com.unip.petcareconnect.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "PetCareConnectPref";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NOME = "nome";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PERFIL = "perfil";
    private static final String KEY_IS_LOGGED = "is_logged_in";
    private static final String KEY_BASE_URL = "base_url";

    // 10.0.2.2 é o alias do Android Emulator para o localhost do PC
    public static final String DEFAULT_BASE_URL = "http://10.0.2.2:5000/";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void salvarSessao(String token, int userId, String nome, String login, String perfil) {
        editor.putBoolean(KEY_IS_LOGGED, true);
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_NOME, nome);
        editor.putString(KEY_LOGIN, login);
        editor.putString(KEY_PERFIL, perfil);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED, false);
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
        return pref.getString(KEY_PERFIL, "Tutor");
    }

    public String getBaseUrl() {
        return pref.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }

    public void setBaseUrl(String url) {
        editor.putString(KEY_BASE_URL, url);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
