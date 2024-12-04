package com.example.vickey.ui.mylist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.HistoryAdapter;
import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.models.CheckWatchedResponse;

import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ApiService apiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_history);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        loadUserHistory();

        return view;
    }


    private void loadUserHistory() {
        long userId = 1L; // 사용자 ID 예시
        apiService.getUserHistory(userId).enqueue(new Callback<List<CheckWatchedResponse>>() {
            @Override
            public void onResponse(Call<List<CheckWatchedResponse>> call, Response<List<CheckWatchedResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> videoThumbnails = response.body().stream()
                            .map(CheckWatchedResponse::getThumbnailUrl)
                            .collect(Collectors.toList());

                    // 어댑터 설정
                    adapter = new HistoryAdapter(getContext(), videoThumbnails);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<CheckWatchedResponse>> call, Throwable t) {
                // 에러 처리
                Log.e("HistoryFragment", "Failed to load history", t);
            }
        });
    }

}
