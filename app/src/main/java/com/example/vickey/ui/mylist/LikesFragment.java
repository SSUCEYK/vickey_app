package com.example.vickey.ui.mylist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.R;
import com.example.vickey.adapter.LikesAdapter;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.EpisodeDTO;
import com.example.vickey.api.models.Episode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noLikesMessage;
    private LikesAdapter adapter;
    private ApiService apiService;
    private final String TAG = "LikesFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);
        noLikesMessage = view.findViewById(R.id.no_likes_message);

        recyclerView = view.findViewById(R.id.recyclerView_likes);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        apiService = ApiClient.getApiService(requireContext()); // 싱글톤 ApiService 사용

        // SharedPreferences에서 userId 가져오기
        String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("userId", null);
        Log.d(TAG, "onCreateView: userId=" + userId);
//        userId = "1"; //테스트용

        loadLikedEpisodes(userId);

        return view;
    }

    private void loadLikedEpisodes(String userId) {
        apiService.getLikedEpisodes(userId).enqueue(new Callback<List<EpisodeDTO>>() {
            @Override
            public void onResponse(Call<List<EpisodeDTO>> call, Response<List<EpisodeDTO>> response) {
                if (response.isSuccessful()) {
                    List<EpisodeDTO> episodeDTOs = response.body();

                    // Null 또는 빈 데이터 확인
                    if (episodeDTOs == null || episodeDTOs.isEmpty()) {
                        Log.d(TAG, "onResponse: EpisodeDTOs == null/empty");
                        showNoLikesMessage();
                        return; // 더 이상 처리하지 않음
                    }

                    // 데이터 변환 및 UI 업데이트
                    List<Episode> likedEpisodes = new ArrayList<>();
                    for (EpisodeDTO dto : episodeDTOs) {
                        if (dto == null) {
                            Log.e(TAG, "EpisodeDTO is null");
                        } else if (dto.getEpisode() == null) {
                            Log.e(TAG, "EpisodeDTO contains null Episode");
                        } else {
                            likedEpisodes.add(dto.getEpisode());
                        }
                    }

                    if (likedEpisodes.isEmpty()) {
                        showNoLikesMessage();
                    } else {
                        showLikesList();
                        adapter = new LikesAdapter(getContext(), likedEpisodes);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    showNoLikesMessage(); // 실패한 경우에도 처리
                    Log.e(TAG, "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<EpisodeDTO>> call, Throwable t) {
                // 에러 처리
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
