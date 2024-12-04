package com.example.vickey.ui.mylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.LikesAdapter;
import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LikesAdapter adapter;
    private ApiService apiService;
    private Map<Long, String> likedEpisodes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_likes);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        loadLikedEpisodes();

        return view;
    }

    private void loadLikedEpisodes() {
        long userId = 1L; // 사용자 ID (예시)
        apiService.getLikedEpisodes(userId).enqueue(new Callback<Map<Long, String>>() {
            @Override
            public void onResponse(Call<Map<Long, String>> call, Response<Map<Long, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    likedEpisodes = response.body();

                    // 어댑터 설정
                    List<String> episodeThumbnails = new ArrayList<>(likedEpisodes.values());
                    adapter = new LikesAdapter(getContext(), episodeThumbnails);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<Map<Long, String>> call, Throwable t) {
                // 에러 처리
            }
        });
    }

}
