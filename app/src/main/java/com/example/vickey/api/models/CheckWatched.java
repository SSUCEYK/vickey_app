package com.example.vickey.api.models;

import java.time.LocalDateTime;

public class CheckWatched {
    private Long userId;
    private Long videoId;
    private String thumbnailUrl; // 비디오의 썸네일 URL
    private Integer progress; // 진행률
    private LocalDateTime watchedDate; // 마지막 시청 시간

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public LocalDateTime getWatchedDate() {
        return watchedDate;
    }

    public void setWatchedDate(LocalDateTime watchedDate) {
        this.watchedDate = watchedDate;
    }
}
