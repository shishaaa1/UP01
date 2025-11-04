package com.example.boobleproject;

import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:5236/";
    private static Retrofit retrofit;

    public static ApiService getApiService() {


        if (retrofit == null) {
            // 🔹 ЛОГИРОВАНИЕ — увидишь ВСЁ: редиректы, тело, ошибки
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // 🔹 OkHttp клиент с редиректами
            OkHttpClient client = new OkHttpClient.Builder()
                    .followRedirects(true)        // ← Редиректы ВКЛ!
                    .followSslRedirects(true)     // ← HTTP → HTTPS
                    .addInterceptor(logging)      // ← Логи в Logcat
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)               // ← Используем наш клиент
                    .addConverterFactory(GsonConverterFactory.create(
                            new GsonBuilder()
                                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss") // <-- сюда
                                    .create()
                    ))
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
