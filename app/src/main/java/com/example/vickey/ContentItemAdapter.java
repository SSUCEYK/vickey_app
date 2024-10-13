package com.example.vickey;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContentItemAdapter extends RecyclerView.Adapter<ContentItemAdapter.ContentViewHolder> {

    private List<Integer> contentImages;

    public ContentItemAdapter(List<Integer> contentImages) {
        this.contentImages = contentImages;
    }

    @NonNull
    @Override
    public ContentItemAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentItemAdapter.ContentViewHolder holder, int position) {
        holder.contentImage.setImageResource(contentImages.get(position));
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
