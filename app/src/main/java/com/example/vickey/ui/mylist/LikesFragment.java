package com.example.vickey.ui.mylist;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.vickey.R;
import com.example.vickey.adapter.LikesAdapter;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.EpisodeDTO;
import com.example.vickey.api.models.Episode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noLikesMessage;
    private LikesAdapter adapter;
    private ApiService apiService;
    private final String TAG = "LikesFragment";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(likeStatusReceiver,
                new IntentFilter("LIKE_STATUS_UPDATED"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(likeStatusReceiver);
    }

    private final BroadcastReceiver likeStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "LIKE_STATUS_UPDATED".equals(intent.getAction())) {
                String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        .getString("userId", null);

                Log.d(TAG, "Broadcast received for LIKE_STATUS_UPDATED");

                if (userId != null) {
                    // 기존 데이터를 임시로 보여줌
                    if (adapter != null) {
                        adapter.notifyDataSetChanged(); // 기존 데이터 갱신
                    }
                    // 최신 데이터를 불러옴
                    loadLikedEpisodes(userId);
                } else {
                    Log.e(TAG, "User ID is null, unable to load liked episodes.");
                }
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        noLikesMessage = view.findViewById(R.id.no_likes_message);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        recyclerView = view.findViewById(R.id.recyclerView_likes);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        apiService = ApiClient.getApiService(requireContext()); // 싱글톤 ApiService 사용

        String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("userId", null);

        loadLikedEpisodes(userId);

        // 새로고침 리스너 설정
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userId != null) {
                loadLikedEpisodes(userId);
            }
        });

        return view;
    }

    private void loadLikedEpisodes(String userId) {

        swipeRefreshLayout.setRefreshing(true); // 로딩 표시 시작
        apiService.getLikedEpisodes(userId).enqueue(new Callback<List<EpisodeDTO>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<List<EpisodeDTO>> call, Response<List<EpisodeDTO>> response) {

                swipeRefreshLayout.setRefreshing(false); // 로딩 표시 종료

                if (response.isSuccessful()) {
                    List<EpisodeDTO> episodeDTOs = response.body();

                    // Null 또는 빈 데이터 확인
                    if (episodeDTOs == null || episodeDTOs.isEmpty()) {
                        Log.d(TAG, "onResponse: EpisodeDTOs == null/empty");
                        showNoLikesMessage();
                        return; // 더 이상 처리하지 않음
                    }

                    // 에피소드를 그룹화하여 중복 제거
                    Map<Long, Episode> uniqueEpisodesMap = new LinkedHashMap<>();
                    for (EpisodeDTO dto : episodeDTOs) {
                        if (dto != null && dto.getEpisode() != null) {
                            uniqueEpisodesMap.put(dto.getEpisode().getEpisodeId(), dto.getEpisode());
                        }
                    }

                    List<Episode> uniqueEpisodes = new ArrayList<>(uniqueEpisodesMap.values());

                    if (uniqueEpisodes.isEmpty()) {
                        showNoLikesMessage();
                    } else {
                        showLikesList();
                        if (adapter == null) {
                            adapter = new LikesAdapter(getContext(), uniqueEpisodes);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.updateData(uniqueEpisodes);
                        }
                    }

                } else {
                    showNoLikesMessage(); // 실패한 경우에도 처리
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EpisodeDTO>> call, Throwable t) {
                // 에러 처리
                swipeRefreshLayout.setRefreshing(false); // 로딩 표시 종료
                Log.e(TAG, "Failed to load liked episodes", t);
            }
        });
    }

    private void showNoLikesMessage() {
        noLikesMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showLikesList() {
        noLikesMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
