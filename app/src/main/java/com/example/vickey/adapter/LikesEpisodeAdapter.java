package com.example.vickey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vickey.ContentDetailActivity;
import com.example.vickey.R;
import com.example.vickey.ShortsActivity;
import com.example.vickey.api.dto.LikedVideosResponse;

import java.util.List;

public class LikesEpisodeAdapter extends RecyclerView.Adapter<LikesEpisodeAdapter.ContentViewHolder> {

    private List<LikedVideosResponse> likedVideosResponses;
    private Context context;
    private final String TAG = "LikesEpisodeAdapter";


    public LikesEpisodeAdapter(List<LikedVideosResponse> likedVideosResponses, Context context) {
        this.likedVideosResponses = likedVideosResponses;
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

        LikedVideosResponse likedVideosResponse = likedVideosResponses.get(position);

        String url = likedVideosResponse.getThumbnailUrl();
        Glide.with(context).clear(holder.contentImage); // Glide 이미지 로딩 전에 이전 이미지 초기화
        holder.bindImage(url);

        // 텍스트 설정
        holder.textView.setText(likedVideosResponse.getVideoNum() + "회");

        // 이미지 뷰 클릭 이벤트 : 클릭 시 영상 재생 액티비티
        holder.contentImage.setOnClickListener(v-> {
            Intent intent = new Intent(context, ShortsActivity.class);
            intent.putExtra("videoId", likedVideosResponse.getVideoId());
            context.startActivity(intent);
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
            //intent.putExtra("episode", episode);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return likedVideosResponses.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        ImageView contentImage;
        ImageButton menuButton;
        TextView textView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.likes_episode_image);
            menuButton = itemView.findViewById(R.id.menu_button); // 점 세 개 버튼 참조
            textView = itemView.findViewById(R.id.likes_episode_text);
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {

            Glide.with(context)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.raw.thumbnail_goblin)
                    .into(contentImage);
        }
    }

}
