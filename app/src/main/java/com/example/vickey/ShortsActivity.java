package com.example.vickey;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.adapter.VideoPagerAdapter;

import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.Episode;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShortsActivity extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private VideoPagerAdapter adapter;
    private String TAG = "ShortsActivity";
    private ImageButton backButton;
    private ImageButton likeButton;
    private ImageButton menuButton;
    private boolean isLiked = false;
    private BottomSheetDialog episodeBottomSheet;
    private Episode currentEpisode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorts);
        viewPager2 = findViewById(R.id.viewPager2);

        // Intent에서 데이터 받기
        int episodeId = getIntent().getIntExtra("episodeId", -1);
        int videoNum = getIntent().getIntExtra("videoNum", -1); // 특정 회차가 지정되지 않으면 -1

        if (episodeId == -1) {
            Toast.makeText(this, "콘텐츠 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // API 호출하여 데이터 가져오기
        ApiService apiService = ApiClient.getApiService(this);
        apiService.contentInfoEpisodes(episodeId).enqueue(new Callback<Episode>() {
            @Override
            public void onResponse(Call<Episode> call, Response<Episode> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Episode episode = response.body();
                    setupVideoPlayer(episode, videoNum);
                } else {
                    Toast.makeText(ShortsActivity.this,
                            "콘텐츠를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Episode> call, Throwable t) {
                Toast.makeText(ShortsActivity.this,
                        "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        initializeButtons();
    }

    private void initializeButtons() {
        // 뒤로가기 버튼
        backButton = findViewById(R.id.shorts_backButton);
        backButton.setOnClickListener(v -> finish());

        // 좋아요 버튼
        likeButton = findViewById(R.id.shorts_likeButton);
        likeButton.setOnClickListener(v -> {
            isLiked = !isLiked;
            likeButton.setImageResource(isLiked ?
                    R.drawable.ic_like_filled : R.drawable.ic_like_unfilled);
        });

        // 메뉴 버튼
        menuButton = findViewById(R.id.shorts_menuButton);
        menuButton.setOnClickListener(v -> showEpisodeBottomSheet());
    }

    private void showEpisodeBottomSheet() {
        episodeBottomSheet = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.episode_bottom_sheet, null);
        episodeBottomSheet.setContentView(bottomSheetView);

        GridLayout gridLayout = bottomSheetView.findViewById(R.id.shorts_episodeGrid);
        gridLayout.setColumnCount(7);

        int currentEpisodeIndex = viewPager2.getCurrentItem();
        int totalEpisodes = currentEpisode.getEpisodeCount(); // Episode 객체에서 총 회차 수 가져오기

        for (int i = 1; i <= totalEpisodes; i++) {
            Button episodeButton = new Button(this);
            episodeButton.setText(String.valueOf(i));
            episodeButton.setTextColor(getResources().getColor(android.R.color.white));

            final int episodeIndex = i - 1;
            if (episodeIndex == currentEpisodeIndex) {
                episodeButton.setBackground(getDrawable(R.drawable.episode_button_background_current_episode));
            } else {
                episodeButton.setBackground(getDrawable(R.drawable.episode_button_background));
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(10, 10, 10, 10);
            episodeButton.setLayoutParams(params);

            episodeButton.setOnClickListener(v -> {
                if (episodeIndex != currentEpisodeIndex) {
                    // 현재 재생 중인 영상 정지
                    pauseVideoAtPosition(currentEpisodeIndex);

                    // ViewPager 페이지 변경
                    viewPager2.setCurrentItem(episodeIndex, false);

                    // 약간의 지연 후 새 영상 재생
                    viewPager2.post(new Runnable() {
                        @Override
                        public void run() {
                            playVideoAtPosition(episodeIndex);
                        }
                    });
                    episodeBottomSheet.dismiss();
                }
            });
            gridLayout.addView(episodeButton);
        }
        episodeBottomSheet.show();
    }

    private void setupVideoPlayer(Episode episode, int targetVideoNum) {
        this.currentEpisode = episode;
        try {
            // JSON 파싱
            List<String> videoUrls = parseVideoUrls(episode.getVideoURLs());
            if (videoUrls.isEmpty()) {
                Toast.makeText(this, "재생할 수 있는 영상이 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // 어댑터 설정
            adapter = new VideoPagerAdapter(this, videoUrls);
            viewPager2.setAdapter(adapter);

            // 시작 위치 설정
            int startPosition = (targetVideoNum != -1) ? targetVideoNum - 1 : 0;
            if (startPosition >= 0 && startPosition < videoUrls.size()) {
                viewPager2.setCurrentItem(startPosition, false);
            }

            // 초기 재생을 위한 약간의 지연 추가
            viewPager2.post(() -> {
                playVideoAtPosition(startPosition);  // 시작 위치의 영상 강제 재생
            });

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                private int currentPosition = startPosition;

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // 이전 영상 정지
                    pauseVideoAtPosition(currentPosition);
                    // 새 영상 재생
                    playVideoAtPosition(position);
                    currentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        // 스크롤 시작할 때 현재 영상 일시정지
                        pauseVideoAtPosition(currentPosition);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up video player", e);
            Toast.makeText(this, "동영상을 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void playVideoAtPosition(int position) {
        // 약간의 지연을 주어 ViewHolder가 준비되도록 함
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
                RecyclerView.ViewHolder viewHolder =
                        recyclerView.findViewHolderForAdapterPosition(position);

                if (viewHolder instanceof VideoPagerAdapter.VideoViewHolder) {
                    VideoPagerAdapter.VideoViewHolder videoViewHolder =
                            (VideoPagerAdapter.VideoViewHolder) viewHolder;
                    ExoPlayer player = (ExoPlayer) videoViewHolder.playerView.getPlayer();
                    if (player != null) {
                        player.setPlayWhenReady(true);
                        player.seekTo(0);
                    }
                }
            }
        }, 200); // 200ms 지연
    }

    private void pauseVideoAtPosition(int position) {
        RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
        RecyclerView.ViewHolder viewHolder =
                recyclerView.findViewHolderForAdapterPosition(position);

        if (viewHolder instanceof VideoPagerAdapter.VideoViewHolder) {
            VideoPagerAdapter.VideoViewHolder videoViewHolder =
                    (VideoPagerAdapter.VideoViewHolder) viewHolder;
            ExoPlayer player = (ExoPlayer) videoViewHolder.playerView.getPlayer();
            if (player != null) {
                player.setPlayWhenReady(false);
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        // 앱이 백그라운드로 갈 때 현재 재생 중인 영상 정지
        pauseVideoAtPosition(viewPager2.getCurrentItem());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 액티비티가 종료될 때 모든 플레이어 해제
        if (adapter != null) {
            adapter.releaseAllPlayers();
        }
    }
    private List<String> parseVideoUrls(String json) {
        List<String> urls = new ArrayList<>();
        try {
            // 맨 앞뒤 따옴표 제거
            String cleanJson = json;
            if (cleanJson.startsWith("\"") && cleanJson.endsWith("\"")) {
                cleanJson = cleanJson.substring(1, cleanJson.length() - 1);
            }

            // 이스케이프된 따옴표(\") 처리 및 대괄호 제거
            cleanJson = cleanJson.replace("\\\"", "")
                    .replace("[", "")
                    .replace("]", "");

            // 콤마로 분리
            String[] urlArray = cleanJson.split(",");

            // 각 URL trim하여 리스트에 추가
            for (String url : urlArray) {
                String cleanUrl = url.trim();
                if (!cleanUrl.isEmpty()) {
                    urls.add(cleanUrl);
                }
            }

            // 디버깅용 로그
            Log.d(TAG, "Original JSON: " + json);
            Log.d(TAG, "Parsed URLs: " + urls.toString());

        } catch (Exception e) {
            Log.e(TAG, "파싱 오류: " + json, e);
        }
        return urls;
    }
}
