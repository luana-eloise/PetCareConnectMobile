package com.unip.petcareconnect.api;

import android.content.Context;
import com.unip.petcareconnect.util.SessionManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Provedor Singleton do cliente HTTP Retrofit configurado para a API do PetCare Connect.
 * Implementa interceptor dinâmico de autenticação JWT, timeouts otimizados e thread-safety.
 */
public final class ApiClient {

    private static final int TIMEOUT_SECONDS = 15;
    private static volatile Retrofit retrofitInstance;
    private static volatile ApiService apiServiceInstance;

    private ApiClient() {
        // Construtor privado para evitar instanciação direta (Padrão Utility/Singleton)
    }

    /**
     * Retorna a instância única do serviço da API, configurando o Retrofit de forma thread-safe.
     *
     * @param context Contexto da aplicação Android.
     * @return Instância configurada de {@link ApiService}.
     */
    public static ApiService getService(Context context) {
        if (apiServiceInstance == null) {
            synchronized (ApiClient.class) {
                if (apiServiceInstance == null) {
                    Context appContext = context.getApplicationContext();
                    SessionManager session = new SessionManager(appContext);
                    String baseUrl = session.getBaseUrl();

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .addInterceptor(logging)
                            .addInterceptor(chain -> {
                                Request original = chain.request();
                                Request.Builder builder = original.newBuilder()
                                        .header("Content-Type", "application/json")
                                        .header("Accept", "application/json");

                                // Lê o token dinamicamente a cada requisição
                                String token = session.getToken();
                                if (token != null && !token.trim().isEmpty()) {
                                    builder.header("Authorization", "Bearer " + token.trim());
                                }

                                return chain.proceed(builder.build());
                            })
                            .build();

                    retrofitInstance = new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    apiServiceInstance = retrofitInstance.create(ApiService.class);
                }
            }
        }
        return apiServiceInstance;
    }

    /**
     * Reseta as instâncias do cliente (ex: quando o IP do servidor ou a BaseURL são alterados nas configurações).
     */
    public static synchronized void reset() {
        retrofitInstance = null;
        apiServiceInstance = null;
    }
}
