package com.example.vickey.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.Episode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeViewModel extends ViewModel {

    private ApiService apiService;
    private final MutableLiveData<List<Episode>> sliderEpisodes = new MutableLiveData<>();
    private final MutableLiveData<List<ContentItem>> contentItems = new MutableLiveData<>();
    private boolean dataLoaded = false; // 데이터 중복 로딩 방지 플래그
    private final String TAG = "HomeViewModel";

    public LiveData<List<Episode>> getSliderEpisodes() {
        return sliderEpisodes;
    }

    public LiveData<List<ContentItem>> getContentItems() {
        return contentItems;
    }

    public boolean isDataLoaded() {
        return dataLoaded;
    }

    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }

    public void loadSliderEpisodes(int n) {

        Log.d(TAG, "loadSliderEpisodes: loadSliderEpisodes in");

        if (sliderEpisodes.getValue() != null && !sliderEpisodes.getValue().isEmpty()) {
            return; // 데이터가 이미 로드되었다면 API 호출을 건너뜀
        }

        apiService.getRandomEpisodes(n).enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sliderEpisodes.setValue(response.body());
                    Log.d(TAG, "loadSliderEpisodes: loadSliderEpisodes success");
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                sliderEpisodes.setValue(null);
                Log.d(TAG, "loadSliderEpisodes: loadSliderEpisodes fail");
            }
        });
    }

    public void loadContentItems(int n, String[] names) {

        if (contentItems.getValue() != null && !contentItems.getValue().isEmpty()) {
            return; // 데이터가 이미 로드되었다면 API 호출을 건너뜀
        }

        List<ContentItem> items = new ArrayList<>();

        Log.d(TAG, "loadContentItems: n=" + n);
        for (String name : names) {
            apiService.getRandomEpisodes(n).enqueue(new Callback<List<Episode>>() {
                @Override
                public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        Log.d(TAG, "API 호출 성공: " + response.body().size());

                        items.add(new ContentItem(name, response.body()));
                        contentItems.setValue(items);
                        dataLoaded = true; // 데이터 로드 완료 플래그 설정

                    }
                }

                @Override
                public void onFailure(Call<List<Episode>> call, Throwable t) {
                    // 실패 처리
                    Log.d("HomeViewModel", "API call failed", t);
                }
            });
        }
    }
}