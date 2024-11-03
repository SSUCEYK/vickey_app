package com.example.vickey;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.NestedViewHolder> {

    private List<String> imageUrlList;
    private List<String> nestedItemTextList;

    public ChildAdapter(List<String> nestedItemTextList, List<String> imageUrlList) {
        this.nestedItemTextList = nestedItemTextList;
        this.imageUrlList = imageUrlList;
    }

    public ChildAdapter(List<String> imageUrlList) {
        this.imageUrlList = imageUrlList;
    }

    public static class NestedViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ShapeableImageView imageView;

        public NestedViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.child_text);
            imageView = itemView.findViewById(R.id.child_image);
        }
    }

    @Override
    public NestedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child, parent, false);
        return new NestedViewHolder(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(NestedViewHolder holder, int position) {
        String imageUrl = imageUrlList.get(position);
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .error(R.raw.thumbnail_goblin)
                .into(holder.imageView);

//        holder.textView.setText(nestedItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageUrlList.size();
    }
}
