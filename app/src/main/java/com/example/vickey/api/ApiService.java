package com.example.vickey.api;

import com.example.vickey.api.models.Episode;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("api/episodes")
    Call<List<Episode>> getEpisodes();

    @GET("api/episodes/titles-counts")
    Call<List<Episode>> getEpisodeTitlesAndCounts();

    // ID 리스트로 썸네일 URL 가져오기
    @POST("api/episodes/thumbnails")
    Call<List<String>> getEpisodeThumbnails(@Body List<Long> ids);

    // 랜덤 n개의 에피소드 ID 가져오기
    @GET("api/episodes/randomEpisode")
    Call<List<Long>> getRandomEpisodeIds(@Query("n") int n);

    // ID로 에피소드 데이터 가져오기
    @GET("api/episodes/{id}")
    Call<Episode> getEpisodeById(@Path("id") Long id);

}


