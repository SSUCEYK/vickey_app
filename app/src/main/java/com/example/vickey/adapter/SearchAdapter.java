package com.example.vickey.adapter;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.vickey.ContentDetailActivity;
import com.example.vickey.api.models.Episode;
import com.example.vickey.R;

import java.util.List;

// 검색결과를 표시하기 위한 recyclerview adapter
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.EpisodeViewHolder> {
    private List<Episode> episodeList;
    private Context context;

    public SearchAdapter(List<Episode> episodes) {
        this.episodeList = episodes;
    }

    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_episode_item, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodeList.get(position);
        holder.titleTextView.setText(episode.getTitle());
        Glide.with(holder.thumbnailImageView.getContext())
                .load(episode.getThumbnailUrl())
                .into(holder.thumbnailImageView);

        holder.itemView.setOnClickListener(v -> { // 클릭 시 상세 정보 액티비티(ContentDetailActivity.java)로 이동
            Log.d("EpisodeAdapter", "Episode ID: " + episode.getEpisodeId()); // 전달 전 값 확인
            Intent intent = new Intent(context, ContentDetailActivity.class);
            intent.putExtra("episodeId", episode.getEpisodeId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public void updateEpisodes(List<Episode> episodes) {
        this.episodeList = episodes;
        notifyDataSetChanged();
    }

    class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        ImageView thumbnailImageView;

        public EpisodeViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.searchTitleTextView);
            thumbnailImageView = itemView.findViewById(R.id.searchThumbnailImageView);
        }
    }
}

