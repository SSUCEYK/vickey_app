package com.example.vickey.api.models;

public class CheckWatchedResponse {

    private Long videoId;         // 비디오 ID
    private String thumbnailUrl;  // 비디오 썸네일 URL
    private Integer progress;     // 시청 진행률

    // Getters and Setters
    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
