package com.example.vickey;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesEpisodeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LikesEpisodeAdapter adapter;
    private ApiService apiService;
    private long episodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_episode);

        // Intent에서 전달된 이미지 리소스 ID 받기
        Intent intent = getIntent();
        episodeId = intent.getLongExtra("episodeId", -1); // (-1: 존재하지 않는 ID)

        // 리사이클러뷰 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView_likes_episode);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        
        apiService = ApiClient.getClient(this).create(ApiService.class);
        loadLikedVideos();
    }

    private void loadLikedVideos() {
        long userId = 1L; // 사용자 ID (예시)
        apiService.getLikedVideosByEpisode(userId, episodeId).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> videoThumbnails = response.body();

                    // 어댑터 설정
                    adapter = new LikesEpisodeAdapter(videoThumbnails, LikesEpisodeActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // 에러 처리
            }
        });
    }
}