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
import com.example.vickey.api.models.Episode;

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
        apiService.getLikedEpisodes(userId).enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                if (response.isSuccessful()) {
                    List<Episode> likedEpisodes = response.body();

                    if (likedEpisodes == null || likedEpisodes.isEmpty()) {
                        showNoLikesMessage(); // 빈 리스트일 경우 메시지 표시
                    } else {
                        showLikesList();

                        // 어댑터 설정
                        adapter = new LikesAdapter(getContext(), likedEpisodes);
                        recyclerView.setAdapter(adapter);
                    }
                } else {
                    showNoLikesMessage(); // 실패한 경우에도 처리
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
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
