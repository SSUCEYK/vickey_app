package com.example.vickey.ui.mylist;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.R;
import com.example.vickey.adapter.LikesEpisodeAdapter;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.LikedVideosResponse;

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

        Log.d(TAG, "onCreate: in");

        // Intent에서 전달된 이미지 리소스 ID 받기
        Intent intent = getIntent();
        episodeId = intent.getLongExtra("episodeId", -1); // (-1: 존재하지 않는 ID)

        // 리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView_likes_episode);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        
        apiService = ApiClient.getClient(this).create(ApiService.class);

        String userId = "1"; // 사용자 ID (예시)
        loadLikedVideos(userId);

    }

    private void loadLikedVideos(String userId) {
        apiService.getLikedVideosByEpisode(userId, episodeId).enqueue(new Callback<List<LikedVideosResponse>>() {
            @Override
            public void onResponse(Call<List<LikedVideosResponse>> call, Response<List<LikedVideosResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LikedVideosResponse> likedVideosResponses = response.body();

                    // 어댑터 설정
                    adapter = new LikesEpisodeAdapter(likedVideosResponses, LikesEpisodeActivity.this);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<LikedVideosResponse>> call, Throwable t) {
                // 에러 처리
            }
        });
    }
}