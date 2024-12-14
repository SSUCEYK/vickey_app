package com.example.vickey.api.dto;

import com.example.vickey.api.models.Episode;

import java.util.List;

public class EpisodeDTO {

    private long episodeId;
    private String title;
    private String thumbnailUrl;
    private int episodeCount;
    private String description;
    private String releasedDate;
    private String castList;
    private List<String> videoUrls;
    private List<Long> videoIds;

    public EpisodeDTO(long episodeId, String title, String thumbnailUrl, int episodeCount, String description, String releasedDate, String castList, List<String> videoUrls, List<Long> videoIds) {
        this.episodeId = episodeId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.episodeCount = episodeCount;
        this.description = description;
        this.releasedDate = releasedDate;
        this.castList = castList;
        this.videoUrls = videoUrls;
        this.videoIds = videoIds;
    }

    public Episode getEpisode(){
        return new Episode(this.episodeId, this.title,
                this.thumbnailUrl, this.episodeCount, this.description,
                this.releasedDate, this.castList, this.videoUrls);
    }

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

    public List<Long> getVideoIds() {
        return videoIds;
    }

    public void setVideoIds(List<Long> videoIds) {
        this.videoIds = videoIds;
    }
}
