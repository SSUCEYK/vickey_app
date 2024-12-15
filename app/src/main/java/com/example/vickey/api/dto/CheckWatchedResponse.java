package com.example.vickey.api.dto;

public class CheckWatchedResponse {

    private Long videoId;         // 비디오 ID
    private String thumbnailUrl;  // 비디오 썸네일 URL
    private int videoNum;         // 비디오 번호
    private Integer progress;     // 시청 진행률
    private Long episodeId;       // 에피소드 ID
    private String episodeTitle;  // 에피소드 제목

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

    public int getVideoNum() {
        return videoNum;
    }

    public void setVideoNum(int videoNum) {
        this.videoNum = videoNum;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public void setEpisodeTitle(String episodeTitle) {
        this.episodeTitle = episodeTitle;
    }
}


//package com.example.vickey.api.dto;
//
//import com.example.vickey.api.models.Episode;
//
//public class CheckWatchedResponse {
//
//    private Long videoId;         // 비디오 ID
//    private String thumbnailUrl;  // 비디오 썸네일 URL
//    private int videoNum;
//    private Integer progress;     // 시청 진행률
//    private Episode episode;
//
//
//    // Getters and Setters
//
//    public int getVideoNum() {
//        return videoNum;
//    }
//
//    public void setVideoNum(int videoNum) {
//        this.videoNum = videoNum;
//    }
//
//    public Episode getEpisode() {
//        return episode;
//    }
//
//    public void setEpisode(Episode episode) {
//        this.episode = episode;
//    }
//
//    public Long getVideoId() {
//        return videoId;
//    }
//
//    public void setVideoId(Long videoId) {
//        this.videoId = videoId;
//    }
//
//    public String getThumbnailUrl() {
//        return thumbnailUrl;
//    }
//
//    public void setThumbnailUrl(String thumbnailUrl) {
//        this.thumbnailUrl = thumbnailUrl;
//    }
//
//    public Integer getProgress() {
//        return progress;
//    }
//
//    public void setProgress(Integer progress) {
//        this.progress = progress;
//    }
//}
