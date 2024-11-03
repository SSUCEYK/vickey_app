package com.example.vickey.ui.mylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vickey.HistoryAdapter;
import com.example.vickey.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_history);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        // 어댑터 설정
        HistoryAdapter adapter = new HistoryAdapter(getContext(), imageUrlShuffle());
        recyclerView.setAdapter(adapter);

        return view;
    }

    // 임시

    // 시청한 에피소드 목록을 반환하는 메서드
    private int[] getHistoryList() {
        // 여기에 실제 데이터 로직 추가


        int[] images = new int[] {
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_lovefromstar,
                R.raw.thumbnail_ohmyghost,
                R.raw.thumbnail_ourbelovedsummer,
                R.raw.thumbnail_signal,
                R.raw.thumbnail_vincenzo,
                R.raw.thumbnail_goblin,
                R.raw.thumbnail_lovefromstar,
                R.raw.thumbnail_ohmyghost,
                R.raw.thumbnail_ourbelovedsummer,
                R.raw.thumbnail_signal,
                R.raw.thumbnail_vincenzo
        };

        return images;
    }

    private List<String> imageUrlShuffle() {
        List<String> contentList = new ArrayList<>();
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        contentList.add("https://placehold.co/132x180");
        Collections.shuffle(contentList);
        return contentList;
    }
}
