package com.example.it_contest.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:5000";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // ✅ OkHttpClient에 타임아웃 설정 추가
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃 30초
                    .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃 30초
                    .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃 30초
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client) // ✅ 수정된 클라이언트 적용
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}