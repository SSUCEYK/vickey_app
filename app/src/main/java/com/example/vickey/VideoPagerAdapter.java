package com.example.vickey;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;


public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {
    private List<String> videoURLs; // CDN URL 리스트
    private Context context;
    private List<ExoPlayer> players;


    public VideoPagerAdapter(Context context, List<String> videoURLs) {
        this.context = context;
        this.videoURLs = (videoURLs != null) ? videoURLs : new ArrayList<>();
        this.players = new ArrayList<>();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shorts, parent, false); // video_item.xml 레이아웃을 인플레이트하여 새로운 뷰를 생성
        return new VideoViewHolder(view);
    }

    //RecyclerView가 내부적으로 항목을 배치할 때 LayoutManager를 통해 onBindViewHolder()를 자동으로 호출
    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoURL = videoURLs.get(position);

        // ExoPlayer 설정
        ExoPlayer player = new ExoPlayer.Builder(context).build();
        players.add(player);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int state) {
                if (state == Player.STATE_READY) {
                    // 영상이 준비되면 로딩 숨기기
                    holder.progressBar.setVisibility(View.GONE);
                }
            }
        });


        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoURL));
        player.setMediaItem(mediaItem);

        holder.playerView.setPlayer(player);
        player.prepare();


    }


    @Override
    public void onViewRecycled(@NonNull VideoViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.playerView.getPlayer() != null) {
            ExoPlayer player = (ExoPlayer) holder.playerView.getPlayer();
            player.stop();
            player.release();
            players.remove(player);
        }
    }

    // RecyclerView가 처음 화면에 나타날 때, 이 메소드를 호출해서 전체 항목의 수 (표시할 항목의 총 개수)를 확인
    // 데이터가 변경되거나 항목이 추가/삭제될 때도 이 메소드가 호출됨
    @Override
    public int getItemCount() {
        return videoURLs.size();
    }

    public void releaseAllPlayers() {
        for (ExoPlayer player : players) {
            player.release();
        }
        players.clear();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        PlayerView playerView;
        ProgressBar progressBar;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            playerView = itemView.findViewById(R.id.playerView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
