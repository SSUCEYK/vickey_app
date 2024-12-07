package com.example.vickey.adapter;

import android.annotation.SuppressLint;
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
import com.example.vickey.api.models.Episode;
import com.google.android.material.imageview.ShapeableImageView;

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

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 캐싱 활성화
                .skipMemoryCache(true)
                .error(R.raw.thumbnail_goblin)
                .into(holder.imageView);

//              .diskCacheStrategy(DiskCacheStrategy.NONE)

        // 텍스트 설정
        holder.textView.setText(title);

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
                                .setDuration(150)
                                .start();
                    }).start();

            // 클릭 후 상세 페이지로 이동
            Intent intent = new Intent(holder.itemView.getContext(), ContentDetailActivity.class);
            intent.putExtra("imageUrl", imageUrl); // 이미지 URL 전달
            holder.itemView.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

}
