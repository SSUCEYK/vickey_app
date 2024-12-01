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

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<String> contentImages;
//    private int[] contentImages;

    public HistoryAdapter(Context context, List<String> contentImages) {
        this.context = context;
        this.contentImages = contentImages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
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
                Intent intent = new Intent(context, ShortsActivity.class);
//                intent.putExtra("imageResId", imageResId);
                intent.putExtra("imageUrl", url);
                context.startActivity(intent);
            }
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
//        return contentImages.length;
        return contentImages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton menuButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.history_image);
            menuButton = itemView.findViewById(R.id.menu_button); // 점 세 개 버튼 참조
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {
//            imageView.setImageResource(imageResId);
            Log.d("HistoryAdapter", imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.raw.thumbnail_goblin)
                    .into(imageView);
        }
    }

}
