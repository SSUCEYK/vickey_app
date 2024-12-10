package com.example.vickey.api.models;

public class Video {
    private long videoId;           // Video의 고유 ID
    private long episodeId;         // 해당 Video의 Episode ID (부모 관계)
    private String videoUrl;        // Video URL
    private int duration;           // Video 재생 시간 (초 단위)
    private int videoNum;           // Episode 내 Video 순서 번호
    private String thumbnailUrl;    // Video 썸네일 URL

    // Getters and Setters
    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(long episodeId) {
        this.episodeId = episodeId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getVideoNum() {
        return videoNum;
    }

    public void setVideoNum(int videoNum) {
        this.videoNum = videoNum;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @Override
    public String toString() {
        return "Video{" +
                "videoId=" + videoId +
                ", episodeId=" + episodeId +
                ", videoUrl='" + videoUrl + '\'' +
                ", duration=" + duration +
                ", videoNum=" + videoNum +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                '}';
    }
}
