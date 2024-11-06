package com.example.vickey;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


//http request
public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static APIService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8080")  // 서버 URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APIService.class);
    }
}
