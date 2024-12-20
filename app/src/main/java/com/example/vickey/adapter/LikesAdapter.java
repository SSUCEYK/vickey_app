package com.example.vickey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.vickey.api.models.Episode;
import com.example.vickey.ui.mylist.LikesEpisodeActivity;

import java.io.Serializable;
import java.util.List;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.MyViewHolder> {

    private Context context;
    private List<Episode> likedEpisodes;
    private final String TAG = "LikesAdapter";

    public LikesAdapter(Context context, List<Episode> likedEpisodes) {
        this.context = context;
        this.likedEpisodes = likedEpisodes;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Episode> newEpisodes) {
        if (!likedEpisodes.equals(newEpisodes)) { // 데이터 변경 여부 확인
            likedEpisodes.clear();
            likedEpisodes.addAll(newEpisodes);
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_likes, parent, false);
        Log.d(TAG, "onCreateViewHolder: Inflated layout=" + view.toString());
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (likedEpisodes == null || likedEpisodes.isEmpty()) {
            // 데이터가 null 또는 비어있는 경우 처리
            return;
        }

        Episode likedEpisode = likedEpisodes.get(position);
        String url = likedEpisode.getThumbnailUrl();
        holder.bindImage(url);

        holder.textView.setText(likedEpisode.getTitle());

        // imageView 클릭 이벤트
        holder.imageView.setOnClickListener(v -> {
            Log.d(TAG, "ImageView clicked for episode: " + likedEpisode.getTitle());

            Intent intent = new Intent(context, LikesEpisodeActivity.class);
            intent.putExtra("episodeId", likedEpisode.getEpisodeId());
            intent.putExtra("title", likedEpisode.getTitle());
            intent.putExtra("episodeCount", likedEpisode.getEpisodeCount());
            intent.putExtra("thumbnailUrl", likedEpisode.getThumbnailUrl());
            intent.putExtra("castList", likedEpisode.getCastList());
            intent.putExtra("description", likedEpisode.getDescription());
            intent.putExtra("releasedDate", likedEpisode.getReleasedDate());
            intent.putExtra("videoUrls", (Serializable) likedEpisode.getVideoUrls());
            context.startActivity(intent);
        });

        // 점 세 개 버튼 클릭 이벤트
        holder.menuButton.setOnClickListener(v -> {
            Log.d(TAG, "menuButton clicked for episode: " + likedEpisode.getTitle());

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
            Intent intent = new Intent(context, ContentDetailActivity.class);
            intent.putExtra("episodeId", likedEpisode.getEpisodeId());
            intent.putExtra("title", likedEpisode.getTitle());
            intent.putExtra("episodeCount", likedEpisode.getEpisodeCount());
            intent.putExtra("thumbnailUrl", likedEpisode.getThumbnailUrl());
            intent.putExtra("castList", likedEpisode.getCastList());
            intent.putExtra("description", likedEpisode.getDescription());
            intent.putExtra("releasedDate", likedEpisode.getReleasedDate());
            intent.putExtra("videoUrls", (Serializable) likedEpisode.getVideoUrls());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return likedEpisodes.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton menuButton;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.likes_image);
            menuButton = itemView.findViewById(R.id.menu_button); // 점 세 개 버튼 참조
            textView = itemView.findViewById(R.id.likes_text);

            Log.d(TAG, "MyViewHolder: itemView=" + itemView.toString());

            if (imageView == null) {
                Log.e(TAG, "MyViewHolder: likes_image not found");
            }
            if (menuButton == null) {
                Log.e(TAG, "MyViewHolder: menu_button not found");
            }
            if (textView == null) {
                Log.e(TAG, "MyViewHolder: likes_text not found");
            }
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView);

        }
    }
}