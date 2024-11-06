package com.example.vickey;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//검색 액티비티
public class SearchActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView recyclerView;
    private EpisodeRecyclerViewAdapter episodeRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 타이틀 바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.searchEditText);
        recyclerView =findViewById(R.id.searchRecyclerView);

        episodeRecyclerViewAdapter = new EpisodeRecyclerViewAdapter(new ArrayList<>());
        recyclerView.setAdapter(episodeRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchEpisodes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchEpisodes(String query) {
        if (query.trim().isEmpty()) {
            // 검색어가 비어 있을 경우, RecyclerView를 비움
            episodeRecyclerViewAdapter.updateEpisodes(new ArrayList<>());
            return;
        }

        APIService apiService = RetrofitClient.getApiService();
        apiService.searchEpisodes(query).enqueue(new Callback<List<Episode>>() {
            @Override
            public void onResponse(Call<List<Episode>> call, Response<List<Episode>> response) {
                if (response.isSuccessful()) {
                    List<Episode> episodes = response.body();
                    Log.d("API Response", "Received: " + episodes.size() + " episodes");
                    episodeRecyclerViewAdapter.updateEpisodes(episodes);
                } else {
                    Log.e("API Error", "Failed to get episodes");
                }
            }

            @Override
            public void onFailure(Call<List<Episode>> call, Throwable t) {
                // 실패 처리
                Log.e("API Error", "Request failed", t);
            }
        });
    }
    
}