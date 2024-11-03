package com.example.vickey.api;

import com.example.vickey.api.models.Episode;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EpisodeService {
    @GET("api/episodes")
    Call<List<Episode>> getEpisodes();

    @GET("api/episodes/titles-counts")
    Call<List<Episode>> getEpisodeTitlesAndCounts();

    @GET("api/episodes/thumbnails") // 이미지 데이터를 반환하는 API 엔드포인트
    Call<List<String>> getEpisodeThumbnails();
}

