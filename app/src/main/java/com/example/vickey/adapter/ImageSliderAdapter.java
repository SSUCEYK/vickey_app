package com.example.vickey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vickey.ContentDetailActivity;
import com.example.vickey.R;
import com.example.vickey.ShortsActivity;
import com.example.vickey.api.models.Episode;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private List<Episode> episodes;
    private final String TAG = "ImageSliderAdapter";

    public ImageSliderAdapter(Context context, List<Episode> episodes) {
        this.context = context;
        this.episodes = episodes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Episode episode = episodes.get(position);
        String url = episode.getThumbnailUrl();

        Log.d(TAG, "onBindViewHolder: position = " +position);
        Log.d(TAG, "onBindViewHolder: episode.getThumbnailUrl() = " + url);

        Glide.with(context).clear(holder.imageView); // Glide 이미지 로딩 전에 이전 이미지 초기화
        holder.bindImage(url);

        // 썸네일 클릭 이벤트
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 통해 새로운 액티비티 실행
                Intent intent = new Intent(context, ShortsActivity.class);
                intent.putExtra("episodeId", episode.getEpisodeId());
                intent.putExtra("videoNum", 1); // 홈화면에서 썸네일 클릭 시 첫 비디오부터 재생 (체크 후 수정: 시청 기록 있으면 시청하던 videoNum부터?)
                context.startActivity(intent);
            }
        });

        // 점 세 개 버튼 클릭 이벤트
        holder.menuButton.setOnClickListener(v -> {
            // 클릭 애니메이션
            ScaleAnimation scaleAnimation = new ScaleAnimation(
                    1.0f, 0.9f, // X축 크기 변화 (1.0 → 0.9)
                    1.0f, 0.9f, // Y축 크기 변화 (1.0 → 0.9)
                    Animation.RELATIVE_TO_SELF, 0.5f, // X축 중심
                    Animation.RELATIVE_TO_SELF, 0.5f  // Y축 중심
            );
            scaleAnimation.setDuration(50); // 애니메이션 지속 시간
            scaleAnimation.setFillAfter(true); // 애니메이션 후 상태 유지

            // 애니메이션 시작
            v.startAnimation(scaleAnimation);

            Log.d(TAG, "onBindViewHolder: 점 세 개 버튼 클릭함");

            // 딜레이 후 액티비티 이동
            v.postDelayed(() -> {
                Intent intent = new Intent(context, ContentDetailActivity.class);
                intent.putExtra("episodeId", episode.getEpisodeId());
                intent.putExtra("title", episode.getTitle());
                intent.putExtra("episodeCount", episode.getEpisodeCount());
                intent.putExtra("thumbnailUrl", episode.getThumbnailUrl());
                intent.putExtra("castList", episode.getCastList());
                intent.putExtra("description", episode.getDescription());
                intent.putExtra("releasedDate", episode.getReleasedDate());

                context.startActivity(intent);
            }, 50); // 후에 실행
        });

        holder.menuButton.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 클릭 시 색상 변화
                    holder.menuButton.setColorFilter(ContextCompat.getColor(context, R.color.grey_7), PorterDuff.Mode.SRC_IN);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // 클릭 해제 시 원래 색상 복원
                    holder.menuButton.clearColorFilter();
                    break;
            }
            return false; // 기본 클릭 이벤트도 동작
        });


    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageButton menuButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlider);
            menuButton = itemView.findViewById(R.id.menuButton); // 점 세 개 버튼 연결
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {
            Log.d("ImageSliderAdapter", imageUrl);

            Glide.with(context)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 캐싱 활성화
                    .error(R.raw.thumbnail_goblin)
                    .into(imageView);
        }
    }

    // 데이터 갱신 메서드 추가
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Episode> episodes) {
        this.episodes = episodes;
        notifyDataSetChanged(); // 어댑터에 변경 알림
    }

}
