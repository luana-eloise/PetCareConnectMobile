package com.unip.petcareconnect.api;

import android.content.Context;
import android.content.Intent;
import com.unip.petcareconnect.ui.LoginActivity;
import com.unip.petcareconnect.util.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton do Retrofit/OkHttp do PetCare Connect.
 */
public final class ApiClient {

    // 90s por causa do boot lento (>2 min) do plano Free do Render
    private static final int TIMEOUT_SECONDS = 90;
    private static volatile Retrofit retrofitInstance;
    private static volatile ApiService apiServiceInstance;

    private ApiClient() { }

    public static ApiService getService(Context context) {
        if (apiServiceInstance == null) {
            synchronized (ApiClient.class) {
                if (apiServiceInstance == null) {
                    Context appContext = context.getApplicationContext();
                    SessionManager session = new SessionManager(appContext);

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    // Não registra o header Authorization no Logcat
                    logging.redactHeader("Authorization");

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .addInterceptor(chain -> {
                                Request original = chain.request();
                                Request.Builder builder = original.newBuilder()
                                        .header("Accept", "application/json");

                                String tokenEnviado = session.getToken();
                                if (tokenEnviado != null && !tokenEnviado.trim().isEmpty()) {
                                    builder.header("Authorization", "Bearer " + tokenEnviado.trim());
                                }

                                Response response = chain.proceed(builder.build());

                                boolean ehLogin = original.url().encodedPath().contains("api/Auth/login");
                                if (response.code() == 401 && !ehLogin
                                        && tokenEnviado != null && !tokenEnviado.trim().isEmpty()) {
                                    synchronized (ApiClient.class) {
                                        // Só age se a sessão ainda é a mesma que gerou o 401
                                        if (tokenEnviado.equals(session.getToken())) {
                                            session.logout();
                                            Intent intent = new Intent(appContext, LoginActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            appContext.startActivity(intent);
                                        }
                                    }
                                }

                                return response;
                            })
                            .addInterceptor(logging)
                            .build();

                    retrofitInstance = new Retrofit.Builder()
                            .baseUrl(session.getBaseUrl())
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    apiServiceInstance = retrofitInstance.create(ApiService.class);
                }
            }
        }
        return apiServiceInstance;
    }

    public static synchronized void reset() {
        retrofitInstance = null;
        apiServiceInstance = null;
    }
}