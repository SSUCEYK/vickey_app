package com.example.vickey.ui.home;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vickey.Episode;
import com.example.vickey.R;

import com.example.vickey.EpisodeRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment {
    private RecyclerView recyclerView;
    private EpisodeRecyclerViewAdapter episodeRecyclerViewAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        recyclerView = view.findViewById(R.id.searchRecyclerView);
        episodeRecyclerViewAdapter = new EpisodeRecyclerViewAdapter(new ArrayList<>());
        recyclerView.setAdapter(episodeRecyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    public void updateSearchResults(List<Episode> episodes) {
        episodeRecyclerViewAdapter.updateEpisodes(episodes);
    }
}
