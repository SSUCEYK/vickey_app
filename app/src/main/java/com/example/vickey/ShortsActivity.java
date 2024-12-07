package com.example.vickey;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vickey.adapter.VideoPagerAdapter;

public class ShortsActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private VideoPagerAdapter adapter;
    private String TAG = "ShortsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorts);

//
//        viewPager2 = findViewById(R.id.viewPager2);
//
//        // S3에 저장된 비디오 파일의 키(name or path) 목록
//        List<String> videoKeys = Arrays.asList("video1.mp4", "video2.mp4", "video3.mp4");
//
//        adapter = new VideoPagerAdapter(this, videoKeys);
//        viewPager2.setAdapter(adapter);
//
//        // 페이지 변경 시 비디오 자동 재생 및 멈춤 처리
//        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//
//            //페이지가 선택될 때: 비디오 재생
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//
//                //RecyclerView를 통해 ViewHolder 접근
//                RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
//                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);
//
//                //타입 검사: viewHolder가 VideoPagerAdapter.VideoViewHolder 타입인 경우에만 비디오를 재생
//                //후 videoView 재생
//                if (viewHolder instanceof VideoPagerAdapter.VideoViewHolder) {
//                    VideoPagerAdapter.VideoViewHolder videoViewHolder = (VideoPagerAdapter.VideoViewHolder) viewHolder;
//                    videoViewHolder.videoView.start();
//                }
//            }
//
//            //페이지 스크롤 상태 변경 시: 비디오 멈춤
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//                //스크롤이 멈추었을 때
//                if (state == ViewPager2.SCROLL_STATE_IDLE) {
//                    int previousPosition = viewPager2.getCurrentItem() - 1;
//                    int nextPosition = viewPager2.getCurrentItem() + 1;
//
//                    RecyclerView recyclerView = (RecyclerView) viewPager2.getChildAt(0);
//
//                    // 이전 페이지의 비디오 멈춤
//                    RecyclerView.ViewHolder previousViewHolder = recyclerView.findViewHolderForAdapterPosition(previousPosition);
//                    if (previousViewHolder instanceof VideoPagerAdapter.VideoViewHolder) {
//                        VideoPagerAdapter.VideoViewHolder previousVideoViewHolder = (VideoPagerAdapter.VideoViewHolder) previousViewHolder;
//                        previousVideoViewHolder.videoView.pause();
//                    }
//
//                    // 다음 페이지의 비디오 멈춤
//                    RecyclerView.ViewHolder nextViewHolder = recyclerView.findViewHolderForAdapterPosition(nextPosition);
//                    if (nextViewHolder instanceof VideoPagerAdapter.VideoViewHolder) {
//                        VideoPagerAdapter.VideoViewHolder nextVideoViewHolder = (VideoPagerAdapter.VideoViewHolder) nextViewHolder;
//                        nextVideoViewHolder.videoView.pause();
//                    }
//                }
//            }
//        });


    }
}
