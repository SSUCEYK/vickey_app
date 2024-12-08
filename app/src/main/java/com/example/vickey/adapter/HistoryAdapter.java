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
import com.example.vickey.ShortsActivity;
import com.example.vickey.api.dto.CheckWatchedResponse;

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

        String url = cw.getThumbnailUrl();
        holder.bindImage(url);

        holder.textView.setText(cw.getEpisode().getTitle() + "/" + cw.getVideoNum() + "회");

        holder.imageView.setOnClickListener(v -> {
            // 비디오 플레이 화면으로 이동 (추가: progress 시점을 기준으로 재생)
            Intent intent = new Intent(context, ShortsActivity.class);
            intent.putExtra("videoId", cw.getVideoId());
            intent.putExtra("ThumbnailUrl", cw.getThumbnailUrl());
            intent.putExtra("Progress", cw.getProgress());
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
            //intent.putExtra("Episode", episode);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return (watchedResponses == null) ? 0 : watchedResponses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton menuButton;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.history_image);
            menuButton = itemView.findViewById(R.id.menu_button); // 점 세 개 버튼 참조
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
