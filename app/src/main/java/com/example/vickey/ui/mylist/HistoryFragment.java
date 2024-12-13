package com.example.vickey.ui.mylist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.adapter.HistoryAdapter;
import com.example.vickey.R;
import com.example.vickey.api.ApiClient;
import com.example.vickey.api.ApiService;
import com.example.vickey.api.dto.CheckWatchedResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private ApiService apiService;
    private final String TAG = "HistoryFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_history);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        apiService = ApiClient.getApiService(requireContext()); // 싱글톤 ApiService 사용

        // SharedPreferences에서 userId 가져오기
        String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("userId", null);
        Log.d(TAG, "onCreateView: userId=" + userId);
//        userId = "1"; //테스트용
        loadUserHistory(userId);

        return view;
    }


    private void loadUserHistory(String userId) {
        apiService.getUserHistory(userId).enqueue(new Callback<List<CheckWatchedResponse>>() {
            @Override
            public void onResponse(Call<List<CheckWatchedResponse>> call, Response<List<CheckWatchedResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    //videoId, thumbnail, progress
                    List<CheckWatchedResponse> watchedResponses = response.body();
                    Log.d(TAG, "onResponse: watchedResponses: " + watchedResponses);

                    // 어댑터 설정
                    if (adapter == null) {
                        adapter = new HistoryAdapter(getContext(), watchedResponses);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateData(watchedResponses);
                    }

                }
            }

            @Override
            public void onFailure(Call<List<CheckWatchedResponse>> call, Throwable t) {
                // 에러 처리
                Log.e(TAG, "Failed to load history", t);
            }
        });
    }

}
