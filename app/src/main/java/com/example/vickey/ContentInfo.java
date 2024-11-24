package com.example.vickey;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.widget.NestedScrollView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentInfo extends AppCompatActivity {
    private BottomSheetBehavior<NestedScrollView> bottomSheetBehavior;

    // View 선언
    private TextView descriptionTitle;
    private TextView descriptionEpisodeCount;
    private TextView descriptionUploadDate;
    private TextView castTextView;
    private TextView summaryTextView;
    private ImageView contentsImage;
    private ImageButton backButton;

    private APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 타이틀 바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_content_info);

        initializeViews();
        backButton.setOnClickListener(v -> {
            finish();
        });
        apiService = RetrofitClient.getApiService();

        int episodeId = getIntent().getIntExtra("episodeId", -1);
        if (episodeId == -1) { // episodeId 값 에러
            Toast.makeText(this, "콘텐츠 정보 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadEpisodeData(episodeId);
        setupBottomSheet();
    }

    private void initializeViews() {
        descriptionTitle = findViewById(R.id.descriptionTitle);
        descriptionEpisodeCount = findViewById(R.id.descriptionEpisodeCount);
        descriptionUploadDate = findViewById(R.id.descriptionUploadDate);
        castTextView = findViewById(R.id.castTextView);
        summaryTextView = findViewById(R.id.summaryTextView);
        contentsImage = findViewById(R.id.contentsImage);
        backButton = findViewById(R.id.backButton);
    }

    private void setupBottomSheet() {
        NestedScrollView bottomsheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void loadEpisodeData(int episodeId){
        apiService.contentInfoEpisodes(episodeId).enqueue(new Callback<Episode>() {
            @Override
            public void onResponse(Call<Episode> call, Response<Episode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Episode episode = response.body();
                    updateUI(episode);
                }
            }

            @Override
            public void onFailure(Call<Episode> call, Throwable t) {
                Log.e("API Error", "Failed to get episodes");
            }
        });
    }

    private void createEpisodeButtons(int episodeNum, int episodeId) {
        GridLayout gridLayout = findViewById(R.id.episodeGrid);
        gridLayout.setColumnCount(7);

        for (int i = 1; i <= episodeNum; i++) {
            Button episodeButton = new Button(this);
            episodeButton.setText(String.valueOf(i));
            episodeButton.setTextColor(getResources().getColor(android.R.color.white));
            episodeButton.setBackground(getResources().getDrawable(R.drawable.episode_button_background));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(10, 10, 10, 10);  // 간격 설정
            episodeButton.setLayoutParams(params);

            final int videoNum = i; //특정 회차
            episodeButton.setOnClickListener(v -> {
                Intent intent = new Intent(ContentInfo.this, ShortsActivity.class);
                intent.putExtra("episodeId", episodeId);
                intent.putExtra("videoNum", videoNum);    // episodeNum -> videoNum
                startActivity(intent);
            });

            // 버튼을 그리드 레이아웃에 추가
            gridLayout.addView(episodeButton);
        }
    }

    private void updateUI(Episode episode) {
        descriptionTitle.setText(episode.getTitle());
        descriptionEpisodeCount.setText(String.format("%d부작", episode.getEpisodeNum()));
        descriptionUploadDate.setText(episode.getReleaseDate());
        castTextView.setText(String.format("출연: %s", episode.getCasting()));
        summaryTextView.setText(episode.getDescription());

        if (episode.getThumbnailUrl() != null && !episode.getThumbnailUrl().isEmpty()) {
            Glide.with(this)
                    .load(episode.getThumbnailUrl())
                    .placeholder(R.drawable.content_info_sample_image)
                    .error(R.drawable.content_info_sample_image)
                    .into(contentsImage);
        }

        createEpisodeButtons(episode.getEpisodeNum(), episode.getEpisodeId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}