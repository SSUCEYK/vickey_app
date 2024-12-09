package com.example.vickey.api.models;

public class Episode {
    private long episodeId;
    private String title;
    private String thumbnailUrl;
    private int episodeCount; // !episode 총 회차 갯수?
    private String description;
    private String releasedDate;
    private String castList; // 연출
    private String videoURLs; // 비디오 url 목록 (재생을 위해 필요, 추가하였음)

    public long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(long episodeId) {
        this.episodeId = episodeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public int getEpisodeCount() {
        return episodeCount;
    }

    public void setEpisodeCount(int episodeCount) {
        this.episodeCount = episodeCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReleasedDate() {
        return releasedDate;
    }

    public void setReleasedDate(String releasedDate) {
        this.releasedDate = releasedDate;
    }

    public String getCastList() {
        return castList;
    }

    public void setCastList(String castList) {
        this.castList = castList;
    }

    public String getVideoURLs() {
        return videoURLs;
    }

    public void setVideoURLs(String videoURLs) {
        this.videoURLs = videoURLs;
    }
}
