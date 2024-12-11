package com.example.vickey.adapter;

import android.annotation.SuppressLint;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vickey.R;
import com.example.vickey.ShortsActivity;
import com.example.vickey.api.dto.CheckWatchedResponse;
import com.example.vickey.api.models.Episode;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<CheckWatchedResponse> watchedResponses;

    public HistoryAdapter(Context context, List<CheckWatchedResponse> watchedResponses) {
        this.context = context;
        this.watchedResponses = watchedResponses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (watchedResponses == null || watchedResponses.isEmpty()) {
            // 데이터가 null 또는 비어있는 경우 처리
            return;
        }

        CheckWatchedResponse cw = watchedResponses.get(position);
        Episode episode = cw.getEpisode();
        String url = cw.getThumbnailUrl();
        holder.bindImage(url);

        String t = episode.getTitle()
                + " ("
                + context.getString(R.string.round_fr)
                + cw.getVideoNum()
                + context.getString(R.string.round_rr) + ")";
        holder.textView.setText(t);

        holder.imageView.setOnClickListener(v -> {
            // 비디오 플레이 화면으로 이동 (추가: progress 시점을 기준으로 재생)
            Intent intent = new Intent(context, ShortsActivity.class);
            intent.putExtra("episodeId", episode.getEpisodeId());
            intent.putExtra("videoNum", cw.getVideoNum());

            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return (watchedResponses == null) ? 0 : watchedResponses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.history_image);
            textView = itemView.findViewById(R.id.history_text);
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {
            Log.d("HistoryAdapter", imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // 캐싱 활성화
                    .error(R.raw.thumbnail_goblin)
                    .into(imageView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<CheckWatchedResponse> newData) {
        this.watchedResponses = newData;
        notifyDataSetChanged();
    }
}
