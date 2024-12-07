package com.example.vickey.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.ui.home.ContentItem;
import com.example.vickey.R;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ContentViewHolder> {

    private List<ContentItem> contentList;

    public ParentAdapter(List<ContentItem> contentList) {
        this.contentList = contentList;
    }

    // ViewHolder 클래스 정의
    public static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView contentListText;
        RecyclerView contentListRV;

        public ContentViewHolder(View itemView) {
            super(itemView);
            contentListText = itemView.findViewById(R.id.contentListText);  // 텍스트뷰 참조
            contentListRV = itemView.findViewById(R.id.contentListRV);    // 내부 리사이클러뷰 참조
        }
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_parent, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        ContentItem item = contentList.get(position);

        // TextView에 데이터 설정
        holder.contentListText.setText(item.getName());

        // 내부 RecyclerView에 어댑터 설정
        ChildAdapter childAdapter = new ChildAdapter(item.getEpisodes());
        holder.contentListRV.setAdapter(childAdapter);
        holder.contentListRV.setLayoutManager(new LinearLayoutManager(holder.contentListRV.getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }
}
