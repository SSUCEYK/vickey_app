package com.example.vickey;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.adapter.VideoPagerAdapter;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.EpisodeDTO;
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
    private ApiService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorts);
        viewPager2 = findViewById(R.id.viewPager2);

        // Intent에서 데이터 받기
        long episodeId = getIntent().getLongExtra("episodeId", -1L);
        int videoNum = getIntent().getIntExtra("videoNum", -1); // 특정 회차가 지정되지 않으면 -1

        if (episodeId == -1) {
            Toast.makeText(this, getString(R.string.content_detail_load_fail), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // API 호출하여 데이터 가져오기
        apiService = ApiClient.getApiService(this);
        Log.d(TAG, "onCreate: episodeId=" + episodeId);

        apiService.contentInfoEpisode(episodeId).enqueue(new Callback<EpisodeDTO>() {
            @Override
            public void onResponse(Call<EpisodeDTO> call, Response<EpisodeDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    EpisodeDTO episodeWithVideoIds = response.body();

                    Log.d(TAG, "onResponse: episode.getEpisodeId()=" + episodeWithVideoIds.getEpisodeId());
                    Log.d(TAG, "onResponse: episode.getTitle()=" + episodeWithVideoIds.getTitle());
                    Log.d(TAG, "onResponse: episode.getThumbnailUrl()=" +episodeWithVideoIds.getThumbnailUrl());

                    setupVideoPlayer(episodeWithVideoIds, videoNum);
                } else {
                    Toast.makeText(ShortsActivity.this,
                            getString(R.string.content_detail_load_fail), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<EpisodeDTO> call, Throwable t) {
                Toast.makeText(ShortsActivity.this,
                        getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        initializeButtons();
    }

    private void updateWatchedStatus(int position) {
        String userId = getSharedPreferences("user_session", Context.MODE_PRIVATE).getString("userId", null);
        Long videoId = adapter.getVideoId(position);
        apiService.markVideoAsWatched(userId, videoId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Watched status updated for videoId: " + videoId);

                    // 브로드캐스트 발송
                    Intent intent = new Intent("WATCHED_STATUS_UPDATED");
                    intent.putExtra("videoId", videoId);
                    LocalBroadcastManager.getInstance(ShortsActivity.this).sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Failed to update watched status", t);
            }
        });
    }

    private void updateLikeStatus(int position) {
        String userId = getSharedPreferences("user_session", Context.MODE_PRIVATE).getString("userId", null);
        Long videoId = adapter.getVideoId(position);

        if (isLiked) {
            // 좋아요 추가
            apiService.likeVideo(userId, videoId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Like status updated for videoId: " + videoId);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Failed to update like status", t);
                }
            });
        } else {
            // 좋아요 취소
            apiService.unlikeVideo(userId, videoId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Unlike status updated for videoId: " + videoId);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, "Failed to update unlike status", t);
                }
            });
        }
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

            // 현재 페이지의 좋아요 상태 업데이트
            updateLikeStatus(viewPager2.getCurrentItem());
        });

        // 메뉴 버튼
        menuButton = findViewById(R.id.shorts_menuButton);
        menuButton.setOnClickListener(v -> showEpisodeBottomSheet());
    }

    private void showEpisodeBottomSheet() {
        episodeBottomSheet = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.episode_bottom_sheet, null);
        episodeBottomSheet.setContentView(bottomSheetView);

        int totalEpisodes = currentEpisode.getVideoUrls().size(); // Episode 객체에서 총 회차 수 가져오기
        Log.d(TAG, "showEpisodeBottomSheet: totalEpisodes(cnt)="+totalEpisodes);
        int currentEpisodeIndex = viewPager2.getCurrentItem();

        GridLayout gridLayout = bottomSheetView.findViewById(R.id.shorts_episodeGrid);
        gridLayout.setColumnCount(7);

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

    private void setupVideoPlayer(EpisodeDTO episodeWithVideoIds, int targetVideoNum) {
        this.currentEpisode = episodeWithVideoIds.getEpisode();
        Log.d(TAG, "setupVideoPlayer: episode.getVideoURLs()=" + episodeWithVideoIds.getVideoUrls());
        try {

            List<String> videoUrls = episodeWithVideoIds.getVideoUrls();
            List<Long> videoIds =  episodeWithVideoIds.getVideoIds();

            // 어댑터 설정
            adapter = new VideoPagerAdapter(this, videoUrls, videoIds);
            viewPager2.setAdapter(adapter);

            // 시작 위치 설정
            int startPosition = (targetVideoNum != -1) ? targetVideoNum - 1 : 0;
            if (startPosition >= 0 && startPosition < videoUrls.size()) {
                viewPager2.setCurrentItem(startPosition, false);
            }

            // 시청 기록 업데이트 **현재 페이지**
            updateWatchedStatus(startPosition);

            // 초기 재생을 위한 약간의 지연 추가
            viewPager2.post(() -> {
                playVideoAtPosition(startPosition);  // 시작 위치의 영상 강제 재생
            });

            viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                private int currentPosition = startPosition;

                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    // 시청 기록 업데이트
                    updateWatchedStatus(position);

                    // 이전 영상 정지
                    pauseVideoAtPosition(currentPosition);

                    // 새 영상 재생
                    playVideoAtPosition(position);
                    currentPosition = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager2.SCROLL_STATE_DRAGGING && currentPosition == adapter.getItemCount() - 1) {
                        // 스크롤 시 현재 페이지가 마지막 영상인지 확인
                        // 마지막 영상에서 스크롤 시도 시
                        Toast.makeText(ShortsActivity.this, getString(R.string.last_video), Toast.LENGTH_SHORT).show();
                        viewPager2.setUserInputEnabled(false); // 스크롤 비활성화

                        // 약간의 지연 후 스크롤 활성화
                        new Handler(Looper.getMainLooper()).postDelayed(() -> viewPager2.setUserInputEnabled(true), 1000);
                    }
                    else if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                        // 스크롤 시작할 때 현재 영상 일시정지
                        pauseVideoAtPosition(currentPosition);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error setting up video player", e);
            Toast.makeText(this, getString(R.string.video_load_error), Toast.LENGTH_SHORT).show();
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

                    // Listener 설정
                    player.addListener(new Player.Listener() {
                        @Override
                        public void onPlaybackStateChanged(int state) {
                            if (state == Player.STATE_ENDED && position == adapter.getItemCount() - 1) {
                                // 마지막 영상이 재생 완료됨
                                Toast.makeText(ShortsActivity.this, getString(R.string.last_video_played), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

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
        if (json == null) {
            Log.d(TAG, "parseVideoUrls: json==null");
            return null;
        }
        
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
