package com.example.vickey.ui.mylist;

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

import com.example.vickey.R;
import com.example.vickey.adapter.HistoryAdapter;
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
    private TextView noHistoryMessage;
    private ApiService apiService;
    private final String TAG = "HistoryFragment";


    @Override
    public void onStart() {
        super.onStart();

        // 브로드캐스트 리시버 등록
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(watchedStatusReceiver,
                new IntentFilter("WATCHED_STATUS_UPDATED"));
    }

    @Override
    public void onStop() {
        super.onStop();

        // 브로드캐스트 리시버 해제
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(watchedStatusReceiver);
    }

    private final BroadcastReceiver watchedStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "WATCHED_STATUS_UPDATED".equals(intent.getAction())) {
                String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        .getString("userId", null);

                // 시청 기록 다시 로드
                loadUserHistory(userId);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        noHistoryMessage = view.findViewById(R.id.no_watched_list);

        recyclerView = view.findViewById(R.id.recyclerView_history);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // 싱글톤 ApiService 사용
        apiService = ApiClient.getApiService(requireContext()); 

        // SharedPreferences에서 userId 가져오기
        String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("userId", null);
        
        // 시청 기록 로드
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

                    if (watchedResponses == null || watchedResponses.isEmpty()) {
                        showNoHistoryMessage(); // 빈 리스트일 경우 메시지 표시
                    }
                    else {
                        showHistoryList();

                        // 어댑터 설정
                        if (adapter == null) {
                            adapter = new HistoryAdapter(getContext(), watchedResponses);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.updateData(watchedResponses);
                        }
                    }

                }
                else {
                    showNoHistoryMessage();
                }

            }

            @Override
            public void onFailure(Call<List<CheckWatchedResponse>> call, Throwable t) {
                // 에러 처리
                Log.e(TAG, "Failed to load history", t);
            }
        });
    }

    private void showNoHistoryMessage() {
        noHistoryMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void showHistoryList() {
        noHistoryMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

}
