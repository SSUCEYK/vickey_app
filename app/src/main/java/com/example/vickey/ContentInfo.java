package com.example.vickey;

import android.os.Bundle;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;


import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.core.widget.NestedScrollView;

public class ContentInfo extends AppCompatActivity {
    private int episodeCount = 40;
    private BottomSheetBehavior<NestedScrollView> bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 타이틀 바 숨기기
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_content_info);

        setupBottomSheet();
        createEpisodeButtons(episodeCount);

    }

    private void setupBottomSheet() {
        NestedScrollView bottomsheet = findViewById(R.id.bottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void createEpisodeButtons(int episodeCount) {
        GridLayout gridLayout = findViewById(R.id.episodeGrid);

        gridLayout.setColumnCount(7);

        for (int i = 1; i <= episodeCount; i++) {
            Button episodeButton = new Button(this);
            episodeButton.setText(String.valueOf(i));
            episodeButton.setTextColor(getResources().getColor(android.R.color.white));
            episodeButton.setBackground(getResources().getDrawable(R.drawable.episode_button_background));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 100;
            params.height = 100;
            params.setMargins(10, 10, 10, 10);  // 간격 설정
            episodeButton.setLayoutParams(params);

            // 버튼을 그리드 레이아웃에 추가
            gridLayout.addView(episodeButton);
        }

    }
}