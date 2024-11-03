package com.example.vickey;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LikesEpisodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes_episode);

        // Intent에서 전달된 이미지 리소스 ID 받기
        Intent intent = getIntent();
        int imageResId = intent.getIntExtra("imageResId", -1); // (-1: 존재하지 않는 ID)

        // 리사이클러뷰 설정
        RecyclerView recyclerView = findViewById(R.id.recyclerView_likes_episode);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        
        // 어댑터 설정
        LikesEpisodeAdapter adapter = new LikesEpisodeAdapter(getLikesEpisodeList(), this);
        recyclerView.setAdapter(adapter);
    }

    // 시청한 에피소드 목록을 반환하는 메서드
    private int[] getLikesEpisodeList() {
        // 여기에 실제 데이터 로직 추가

        int[] images = new int[] {
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_goblin
        };

        return images;
    }
}