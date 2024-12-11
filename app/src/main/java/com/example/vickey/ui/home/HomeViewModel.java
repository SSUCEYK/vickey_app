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

    private final String TAG = "HomeViewModel";
    private ApiService apiService;
    public void setApiService(ApiService apiService) {
        this.apiService = apiService;
    }
    private final MutableLiveData<List<Episode>> sliderEpisodes = new MutableLiveData<>();

    private final MutableLiveData<List<ContentItem>> contentItems = new MutableLiveData<>();

    public LiveData<List<Episode>> getSliderEpisodes() {
        return sliderEpisodes;
    }
    public LiveData<List<ContentItem>> getContentItems() {
        return contentItems;
    }

    private boolean dataLoaded = false; // 데이터 중복 로딩 방지 플래그

    public boolean isDataLoaded() {
        return dataLoaded;
    }


    public void loadSliderEpisodes(int n) {

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

    // 랜덤 에피소드 데이터를 ContentItem에 추가
    private void fetchEpisodesForContentItem(String name, int n, List<ContentItem> items) {
        apiService.getRandomEpisodes(n).enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "fetchEpisodesForContentItem: 랜덤 에피소드 성공 - " + response.body().size());

                    items.add(new ContentItem(name, response.body()));
                    contentItems.setValue(items); // 변경된 리스트를 LiveData에 설정
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                Log.e(TAG, "fetchEpisodesForContentItem: 랜덤 에피소드 실패", t);
            }
        });
    }

    // 좋아요 순 에피소드 데이터를 ContentItem에 추가
    private void fetchLikedEpisodesForContentItem(String name, int n, List<ContentItem> items) {
        apiService.getTopNLikedEpisodes(n).enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "fetchLikedEpisodesForContentItem: 좋아요 순 에피소드 성공 - " + response.body().size());

                    items.add(new ContentItem(name, response.body()));
                    contentItems.setValue(items); // 변경된 리스트를 LiveData에 설정
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                Log.e(TAG, "fetchLikedEpisodesForContentItem: 좋아요 순 에피소드 실패", t);
            }
        });
    }

    // 조회수 순 에피소드 데이터를 ContentItem에 추가
    private void fetchWatchedEpisodesForContentItem(String name, int n, List<ContentItem> items) {
        apiService.getTopNWatchedEpisodes(n).enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "fetchWatchedEpisodesForContentItem: 조회수 순 에피소드 성공 - " + response.body().size());

                    items.add(new ContentItem(name, response.body()));
                    contentItems.setValue(items); // 변경된 리스트를 LiveData에 설정
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                Log.e(TAG, "fetchWatchedEpisodesForContentItem: 조회수 순 에피소드 실패", t);
            }
        });
    }

    // ContentItems 로드
    public void loadContentItems(int n, String[] names) {
        if (contentItems.getValue() != null && !contentItems.getValue().isEmpty()) {
            return; // 데이터가 이미 로드되었다면 API 호출을 건너뜀
        }

        List<ContentItem> items = new ArrayList<>();

        // 좋아요 순 에피소드 로드
        fetchLikedEpisodesForContentItem(names[0], n, items);

        // 조회수 순 에피소드 로드
        fetchWatchedEpisodesForContentItem(names[1], n, items);

        // 추천 순? 로드
        // 랜덤 에피소드 로드
        fetchEpisodesForContentItem(names[2], n, items);

        dataLoaded = true; // 데이터 로드 완료 플래그 설정
    }
}