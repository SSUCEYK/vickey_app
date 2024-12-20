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
    private OnItemLongClickListener longClickListener;

    public interface OnItemLongClickListener {
        void onItemLongClick(CheckWatchedResponse item, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

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
            return;
        }

        CheckWatchedResponse cw = watchedResponses.get(position);
        String url = cw.getThumbnailUrl();
        holder.bindImage(url);

        String t = cw.getEpisodeTitle()
                + " ("
                + context.getString(R.string.round_fr)
                + cw.getVideoNum()
                + context.getString(R.string.round_rr) + ")";
        holder.textView.setText(t);

        holder.imageView.setOnClickListener(v -> {
            // 비디오 플레이 화면으로 이동 (추가: progress 시점을 기준으로 재생)
            Intent intent = new Intent(context, ShortsActivity.class);
            intent.putExtra("episodeId", cw.getEpisodeId());
            intent.putExtra("videoNum", cw.getVideoNum());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(cw, position);
            }
            return true;
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
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(imageView);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<CheckWatchedResponse> newData) {

        if (watchedResponses == null) {
            watchedResponses = newData;
            notifyDataSetChanged();
        } else {
            for (int i = 0; i < newData.size(); i++) {
                if (i < watchedResponses.size()) {
                    // 기존 데이터와 비교하여 변경된 경우에만 업데이트
                    if (!watchedResponses.get(i).equals(newData.get(i))) {
                        watchedResponses.set(i, newData.get(i));
                        notifyItemChanged(i);
                    }
                } else {
                    // 새 데이터 추가
                    watchedResponses.add(newData.get(i));
                    notifyItemInserted(i);
                }
            }

            // 기존 리스트가 새 데이터보다 클 경우 초과 데이터 제거
            if (watchedResponses.size() > newData.size()) {
                int removeCount = watchedResponses.size() - newData.size();
                for (int i = 0; i < removeCount; i++) {
                    watchedResponses.remove(watchedResponses.size() - 1);
                    notifyItemRemoved(watchedResponses.size());
                }
            }
        }
    }

    public void removeItem(int position) {
        watchedResponses.remove(position);
        notifyItemRemoved(position);
    }

}