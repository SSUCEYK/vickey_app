package com.example.vickey.ui.mylist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
    private SwipeRefreshLayout swipeRefreshLayout;
    private final String TAG = "HistoryFragment";
    private boolean isLoading = false;


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
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && "WATCHED_STATUS_UPDATED".equals(intent.getAction())) {
                String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                        .getString("userId", null);

                Log.d(TAG, "Broadcast received for WATCHED_STATUS_UPDATED");

                if (userId != null) {
                    // 기존 데이터를 임시로 갱신
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    // 최신 데이터를 로드
                    loadUserHistory(userId);
                } else {
                    Log.e(TAG, "User ID is null, unable to load history.");
                }
            }
        }
    };


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        noHistoryMessage = view.findViewById(R.id.no_watched_list);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_history);

        recyclerView = view.findViewById(R.id.recyclerView_history);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // RecyclerView 기본 애니메이션 제거
        recyclerView.setItemAnimator(null);

        // 싱글톤 ApiService 사용
        apiService = ApiClient.getApiService(requireContext());

        // SharedPreferences에서 userId 가져오기
        String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("userId", null);

        // 시청 기록 로드
        loadUserHistory(userId);

        // SwipeRefreshLayout 새로고침 동작
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (userId != null) {
                loadUserHistory(userId);
            } else {
                swipeRefreshLayout.setRefreshing(false); // 로딩 중단
                Log.e(TAG, "User ID is null, unable to refresh history.");
            }
        });

        return view;
    }


    private void loadUserHistory(String userId) {

        if (isLoading) return; // 중복 호출 방지
        isLoading = true;
        swipeRefreshLayout.setRefreshing(true); // 새로고침 아이콘 표시

        apiService.getUserHistory(userId).enqueue(new Callback<List<CheckWatchedResponse>>() {
            @Override
            public void onResponse(Call<List<CheckWatchedResponse>> call, Response<List<CheckWatchedResponse>> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false); // 새로고침 아이콘 종료

                if (response.isSuccessful() && response.body() != null) {
                    //videoId, thumbnail, progress
                    List<CheckWatchedResponse> watchedResponses = response.body();
                    Log.d(TAG, "onResponse: watchedResponses: " + watchedResponses);

                    if (watchedResponses.isEmpty()) {
                        showNoHistoryMessage(); // 빈 리스트일 경우 메시지 표시
                    }
                    else {
                        showHistoryList();

                        // 어댑터 설정
                        if (adapter == null) {
                            adapter = new HistoryAdapter(getContext(), watchedResponses);
                            recyclerView.setAdapter(adapter);

                            // LongClickListener 설정
                            adapter.setOnItemLongClickListener((item, position) -> showDeleteDialog(item, position));
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
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false); // 새로고침 아이콘 종료
                Log.e(TAG, "Failed to load history", t);
            }
        });
    }

    private void showDeleteDialog(CheckWatchedResponse item, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_history_title)
                .setMessage(R.string.delete_history_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteHistory(item, position))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteHistory(CheckWatchedResponse item, int position) {
        String userId = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
                .getString("userId", null);

        apiService.markVideoAsUnwatched(userId, item.getVideoId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // UI에서 제거
                    adapter.removeItem(position);
                    if (adapter.getItemCount() == 0) {
                        showNoHistoryMessage();
                    }
                } else {
                    Log.e(TAG, "Failed to delete history: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting history", t);
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