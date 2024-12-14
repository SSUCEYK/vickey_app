package com.example.vickey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vickey.ContentDetailActivity;
import com.example.vickey.R;
import com.example.vickey.ShortsActivity;
import com.example.vickey.api.models.Episode;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.Serializable;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.NestedViewHolder> {

    private final List<Episode> episodes;

    public ChildAdapter(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public static class NestedViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ShapeableImageView imageView;
        ImageButton menuButton;

        public NestedViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.child_text);
            imageView = itemView.findViewById(R.id.child_image);
            menuButton = itemView.findViewById(R.id.menuButton); // 점 세 개 버튼
        }
    }

    @Override
    public NestedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child, parent, false);
        return new NestedViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(NestedViewHolder holder, int position) {

        Episode episode = episodes.get(position);
        String imageUrl = episode.getThumbnailUrl();
        String title = episode.getTitle();

        // View에서 Context 가져오기
        Context context = holder.itemView.getContext();

        // 이미지 바인딩
        Glide.with(context).clear(holder.imageView); // Glide 이미지 로딩 전에 이전 이미지 초기화
        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 캐싱 활성화
                .skipMemoryCache(true)
                .error(R.raw.thumbnail_goblin)
                .into(holder.imageView);

        // 텍스트 설정
        holder.textView.setText(title);

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
            // 애니메이션 효과 추가
            holder.menuButton.animate()
                    .scaleX(1.2f) // X축 확대
                    .scaleY(1.2f) // Y축 확대
                    .setDuration(150) // 애니메이션 지속 시간
                    .withEndAction(() -> { // 애니메이션 끝난 후 실행
                        holder.menuButton.animate()
                                .scaleX(1.0f) // 원래 크기로 복귀
                                .scaleY(1.0f)
                                .setDuration(50)
                                .start();
                    }).start();

            // 클릭 시 딜레이 후 상세 페이지로 이동
            v.postDelayed(() -> {
                Intent intent = new Intent(holder.itemView.getContext(), ContentDetailActivity.class);
                intent.putExtra("episodeId", episode.getEpisodeId());
                intent.putExtra("title", episode.getTitle());
                intent.putExtra("episodeCount", episode.getEpisodeCount());
                intent.putExtra("thumbnailUrl", episode.getThumbnailUrl());
                intent.putExtra("castList", episode.getCastList());
                intent.putExtra("description", episode.getDescription());
                intent.putExtra("releasedDate", episode.getReleasedDate());
                intent.putExtra("videoUrls", (Serializable) episode.getVideoUrls());
                holder.itemView.getContext().startActivity(intent);
            }, 50); // 후에 실행
        });

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

}
