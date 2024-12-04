package com.example.vickey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.MyViewHolder> {

    private Context context;
    private List<String> contentImages;
    private Map<Long, String> likedEpisodes;


    // contentImages = video id의 해당 episode id의 thumbnailUrl
    public LikesAdapter(Context context, List<String> contentImages) {
        this.context = context;
        this.contentImages = contentImages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_likes, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String url = contentImages.get(position);
        Long episodeId = new ArrayList<>(likedEpisodes.keySet()).get(position);
        holder.bindImage(url);

        holder.imageView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LikesEpisodeActivity.class);
            intent.putExtra("episodeId", episodeId);
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

            // 클릭 후 상세 페이지로 이동
            Intent intent = new Intent(context, ContentDetailActivity.class);
            intent.putExtra("imageUrl", url);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return contentImages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton menuButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.likes_image);
            menuButton = itemView.findViewById(R.id.menu_button); // 점 세 개 버튼 참조
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {
            Log.d("LikesAdapter", imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.raw.thumbnail_goblin)
                    .into(imageView);

        }
    }
}
