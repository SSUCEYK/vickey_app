package com.example.vickey;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.MyViewHolder> {
    private Context context;
    private List<String> sliderImage;

    public ImageSliderAdapter(Context context, List<String> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_slider, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String url = sliderImage.get(position);
        holder.bindImage(url);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 통해 새로운 액티비티 실행
                Intent intent = new Intent(context, ShortsActivity.class);
                intent.putExtra("imageUrl", url);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlider);
        }

        @SuppressLint("ResourceType")
        public void bindImage(String imageUrl) {
//            imageView.setImageResource(imageResId);
            Log.d("ImageSliderAdapter", imageUrl);
            Glide.with(context)
                    .load(imageUrl)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(R.raw.thumbnail_goblin)
                    .into(imageView);

//            Glide.with(context)
//                    .load(imageUrl)
//                    .apply(new RequestOptions()
//                            .skipMemoryCache(true)
//                            .diskCacheStrategy(DiskCacheStrategy.NONE)
//                            .error(R.raw.thumbnail_goblin))
//                    .into(imageView);

//            Picasso.get()
//                    .load(imageUrl)
//                    .error(R.raw.thumbnail_goblin)
//                    .into(imageView);
        }
    }
}
