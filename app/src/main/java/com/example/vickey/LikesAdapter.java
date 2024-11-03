package com.example.vickey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.MyViewHolder> {

    private Context context;
    private List<String> contentImages;
    //    private int[] contentImages;

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
//        int imageResId = contentImages[position];
//        holder.contentImage.setImageResource(imageResId);

        String url = contentImages.get(position);
        holder.bindImage(url);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 통해 새로운 액티비티 실행
                // 좋아요 컨텐츠 -> 좋아요 에피소드 리스트 보여주는 액티비티
                Intent intent = new Intent(context, LikesEpisodeActivity.class);
//                intent.putExtra("imageResId", imageResId);
                intent.putExtra("imageUrl", url);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
//        return contentImages.length;
        return contentImages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.likes_image);
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {
//            imageView.setImageResource(imageResId);
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
