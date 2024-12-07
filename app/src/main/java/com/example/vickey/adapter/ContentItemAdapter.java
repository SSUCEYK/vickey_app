package com.example.vickey.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.R;
import com.example.vickey.ShortsActivity;

import java.util.List;

public class ContentItemAdapter extends RecyclerView.Adapter<ContentItemAdapter.ContentViewHolder> {

    private List<Integer> contentImages;
    private Context context;

    public ContentItemAdapter(List<Integer> contentImages, Context context) {
        this.contentImages = contentImages;
        this.context = context;
    }

    @NonNull
    @Override
    public ContentItemAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentItemAdapter.ContentViewHolder holder, int position) {
//        holder.contentImage.setImageResource(contentImages.get(position));

        int imageResId = contentImages.get(position);
        holder.contentImage.setImageResource(imageResId);

        // 이미지 클릭 이벤트 처리
        holder.contentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 통해 새로운 액티비티 실행
                Intent intent = new Intent(context, ShortsActivity.class);
                intent.putExtra("imageResId", imageResId);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return contentImages.size();
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        ImageView contentImage;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            contentImage = itemView.findViewById(R.id.content_image);
        }
    }

}
