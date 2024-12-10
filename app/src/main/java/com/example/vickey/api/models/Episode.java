package com.example.vickey.api.models;

import java.util.List;

public class Episode {
    private long episodeId;          // Episode ID
    private String title;            // Episode 제목
    private String thumbnailUrl;     // 썸네일 URL
    private int episodeCount;        // Episode 총 회차 수
    private String description;      // 설명
    private String releasedDate;     // 출시 날짜
    private String castList;         // 출연진/연출
    private List<String> videoUrls;  // Video URL 리스트 (변경)

    // Getters and Setters
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

    public List<String> getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(List<String> videoUrls) {
        this.videoUrls = videoUrls;
    }

    @Override
    public String toString() {
        return "Episode{" +
                "episodeId=" + episodeId +
                ", title='" + title + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", episodeCount=" + episodeCount +
                ", description='" + description + '\'' +
                ", releasedDate='" + releasedDate + '\'' +
                ", castList='" + castList + '\'' +
                ", videoUrls=" + videoUrls +
                '}';
    }
}
