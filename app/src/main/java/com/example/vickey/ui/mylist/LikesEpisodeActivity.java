package com.example.vickey.ui.mylist;

import android.content.Context;
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
import com.example.vickey.api.models.Episode;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesEpisodeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LikesEpisodeAdapter adapter;
    private ApiService apiService;
    private Episode episode;
    private long episodeId;
    private final String TAG = "LikesEpisodeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_episode);

        Log.d(TAG, "onCreate: in");

        // Intent에서 전달된 이미지 리소스 ID 받기
        Intent intent = getIntent();
        episodeId = intent.getLongExtra("episodeId", -1L); // (-1: 존재하지 않는 ID)
        String title = intent.getStringExtra("title");
        String thumbnailUrl = intent.getStringExtra("thumbnailUrl");
        int episodeCount = intent.getIntExtra("episodeCount", -1);
        String description = intent.getStringExtra("description");
        String releasedDate = intent.getStringExtra("releasedDate");
        String castList = intent.getStringExtra("castList");
        List<String> videoUrls = intent.getStringArrayListExtra("videoUrls");
        episode = new Episode(episodeId, title, thumbnailUrl, episodeCount, description, releasedDate, castList, videoUrls);

        // 리사이클러뷰 설정
        recyclerView = findViewById(R.id.recyclerView_likes_episode);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        
        apiService = ApiClient.getClient(this).create(ApiService.class);

        // SharedPreferences에서 userId 가져오기
        String userId = getApplicationContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("userId", null);
        Log.d(TAG, "onCreateView: userId=" + userId);
        userId = "1"; //테스트용

        loadLikedVideos(userId);

    }

    private void loadLikedVideos(String userId) {
        apiService.getLikedVideosByEpisode(userId, episodeId).enqueue(new Callback<List<LikedVideosResponse>>() {
            @Override
            public void onResponse(Call<List<LikedVideosResponse>> call, Response<List<LikedVideosResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LikedVideosResponse> likedVideosResponses = response.body();

                    // 어댑터 설정
                    adapter = new LikesEpisodeAdapter(likedVideosResponses, episode, LikesEpisodeActivity.this);
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