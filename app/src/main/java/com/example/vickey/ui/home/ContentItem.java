package com.example.vickey.ui.home;

import com.example.vickey.api.models.Episode;

import java.util.List;

public class ContentItem {
    private String name; // 섹션 제목
    private List<Episode> episodes; // 각 섹션의 Episode 리스트

    public ContentItem(String name, List<Episode> episodes) {
        this.name = name;
        this.episodes = episodes;
    }

    public String getName() {
        return name;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }
}
