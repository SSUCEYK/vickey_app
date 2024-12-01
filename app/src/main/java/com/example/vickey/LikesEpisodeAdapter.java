package com.example.vickey;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LikesEpisodeAdapter extends RecyclerView.Adapter<LikesEpisodeAdapter.ContentViewHolder> {

    private int[] contentImages;
    private Context context;

    public LikesEpisodeAdapter(int[] contentImages, Context context) {
        this.contentImages = contentImages;
        this.context = context;
    }

    @NonNull
    @Override
    public LikesEpisodeAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_likes_episode, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikesEpisodeAdapter.ContentViewHolder holder, int position) {

        int imageResId = contentImages[position];
        holder.contentImage.setImageResource(imageResId);

        holder.contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 통해 새로운 액티비티 실행
                // 좋아요 컨텐츠 -> 좋아요 에피소드 리스트 보여주는 액티비티
                Intent intent = new Intent(context, ShortsActivity.class);
                intent.putExtra("imageResId", imageResId);
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
                                .setDuration(150)
                                .start();
                    }).start();

            // 클릭 후 ContentDetailActivity로 이동
            Intent intent = new Intent(context, ContentDetailActivity.class);
            intent.putExtra("imageResId", imageResId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contentImages.length;
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        ImageView contentImage;
        ImageButton menuButton;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.likes_episode_image);
            menuButton = itemView.findViewById(R.id.menu_button); // 점 세 개 버튼 참조
        }
    }

}