package com.example.vickey.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vickey.R;
import com.example.vickey.ShortsActivity;
import com.example.vickey.api.dto.LikedVideosResponse;
import com.example.vickey.api.models.Episode;

import java.util.List;

public class LikesEpisodeAdapter extends RecyclerView.Adapter<LikesEpisodeAdapter.ContentViewHolder> {

    private List<LikedVideosResponse> likedVideosResponses;
    private Episode episode;
    private Context context;
    private final String TAG = "LikesEpisodeAdapter";


    public LikesEpisodeAdapter(List<LikedVideosResponse> likedVideosResponses, Episode episode, Context context) {
        this.likedVideosResponses = likedVideosResponses;
        this.episode = episode;
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
        int videoNum = likedVideosResponse.getVideoNum();
        String t = context.getString(R.string.round_fr)
                + videoNum
                + context.getString(R.string.round_rr);
        holder.textView.setText(t);

        // 이미지 뷰 클릭 이벤트 : 클릭 시 영상 재생 액티비티
        holder.contentImage.setOnClickListener(v-> {
            Intent intent = new Intent(context, ShortsActivity.class);
            intent.putExtra("episodeId", episode.getEpisodeId());
            intent.putExtra("videoNum", videoNum);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return likedVideosResponses.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        ImageView contentImage;
        TextView textView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.likes_episode_image);
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
