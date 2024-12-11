package com.example.vickey;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.Episode;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentDetailActivity extends AppCompatActivity {
    private BottomSheetBehavior<NestedScrollView> bottomSheetBehavior;

    // View 선언
    private TextView descriptionTitle;
    private TextView descriptionEpisodeCount;
    private TextView descriptionUploadDate;
    private TextView castTextView;
    private TextView summaryTextView;
    private ImageView contentsImage;
    private ImageButton backButton;
    private Episode currentEpisode;
    private int videoNum;
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 타이틀 바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_content_detail);

        initializeViews();
        backButton.setOnClickListener(v -> {
            finish();
        });
        apiService = ApiClient.getApiService(this);

        // Intent에서 전달된 이미지 리소스 ID 받기
        Intent intent = getIntent();
        long episodeId = intent.getLongExtra("episodeId", -1L); // (-1: 존재하지 않는 ID)
        String title = intent.getStringExtra("title");
        String thumbnailUrl = intent.getStringExtra("thumbnailUrl");
        int episodeCount = intent.getIntExtra("episodeCount", -1);
        String description = intent.getStringExtra("description");
        String releasedDate = intent.getStringExtra("releasedDate");
        String castList = intent.getStringExtra("castList");
        List<String> videoUrls = intent.getStringArrayListExtra("videoUrls");
        currentEpisode = new Episode(episodeId, title, thumbnailUrl, episodeCount, description, releasedDate, castList, videoUrls);
        videoNum = getIntent().getIntExtra("videoNum", -1); // 특정 회차가 지정되지 않으면 -1

        if (episodeId == -1) { // episodeId 값 에러
            Toast.makeText(this, getString(R.string.content_detail_load_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateUI(currentEpisode);
        setupBottomSheet();
    }

    private void initializeViews() {
        descriptionTitle = findViewById(R.id.descriptionTitle);
        descriptionEpisodeCount = findViewById(R.id.descriptionEpisodeCount);
        descriptionUploadDate = findViewById(R.id.descriptionUploadDate);
        castTextView = findViewById(R.id.castTextView);
        summaryTextView = findViewById(R.id.summaryTextView);
        contentsImage = findViewById(R.id.contentsImage);
        backButton = findViewById(R.id.content_info_backButton);
    }

    private void setupBottomSheet() {
        NestedScrollView bottomsheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void loadEpisodeData(long episodeId){
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

    //전체 회수 만큼 회차 버튼 생성, 클릭하여 해당 회차로 이동
    private void createEpisodeButtons(long episodeId) {

        GridLayout gridLayout = findViewById(R.id.episodeGrid);
        gridLayout.setColumnCount(7);

        int totalEpisodes = currentEpisode.getVideoUrls().size(); // Episode 객체에서 총 회차 수 가져오기
        for (int i = 1; i <= totalEpisodes; i++) {
            Button episodeButton = new Button(this);
            episodeButton.setText(String.valueOf(i));
            episodeButton.setTextColor(getResources().getColor(android.R.color.white));

            final int episodeIndex = i - 1;
            if (episodeIndex == videoNum) {
                episodeButton.setBackground(getDrawable(R.drawable.episode_button_background_current_episode));
            } else {
                episodeButton.setBackground(getDrawable(R.drawable.episode_button_background));
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(10, 10, 10, 10);  // 간격 설정
            episodeButton.setLayoutParams(params);

            final int videoNum = i; //특정 회차
            episodeButton.setOnClickListener(v -> {
                Intent intent = new Intent(ContentDetailActivity.this, ShortsActivity.class);
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
        descriptionEpisodeCount.setText(String.format("%d" + getString(R.string.parts), episode.getEpisodeCount())); //회차 수 전체 불러오기
        descriptionUploadDate.setText(episode.getReleasedDate()); //업로드 일자 불러오기
        castTextView.setText(String.format(getString(R.string.casts)+": %s", episode.getCastList())); //연출 불러오기
        summaryTextView.setText(episode.getDescription());

        if (episode.getThumbnailUrl() != null && !episode.getThumbnailUrl().isEmpty()) {
            Glide.with(this)
                    .load(episode.getThumbnailUrl())
                    .placeholder(R.drawable.content_detail_activity_sample_image)
                    .error(R.drawable.content_detail_activity_sample_image)
                    .into(contentsImage);
        }

        createEpisodeButtons(episode.getEpisodeId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
