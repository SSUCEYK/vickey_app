package com.example.vickey.api;

import android.content.Context;

import com.example.vickey.R;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            String baseUrl = context.getString(R.string.api_base_url);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService(Context context) {
        if (apiService == null) {
            apiService = getClient(context).create(ApiService.class);
        }
        return apiService;
    }
}
