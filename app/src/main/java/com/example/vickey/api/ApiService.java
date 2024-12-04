package com.example.vickey.api;

import com.example.vickey.api.models.CheckWatchedResponse;
import com.example.vickey.api.models.Episode;
import com.example.vickey.api.models.Like;

import java.util.List;
import java.util.Map;

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
    @GET("api/episodes/randomEpisodeIds")
    Call<List<Long>> getRandomEpisodeIds(@Query("n") int n);

    // 랜덤 n개의 에피소드 가져오기
    @GET("api/episodes/randomEpisodes")
    Call<List<Episode>> getRandomEpisodes(@Query("n") int n);

    // ID로 에피소드 데이터 가져오기
    @GET("api/episodes/{id}")
    Call<Episode> getEpisodeById(@Path("id") Long id);

    // ID 리스트로 title 리스트 가져오기
    @POST("api/getEpisodeTitles")
    Call<List<String>> getEpisodeTitles(@Body List<Long> episodeIds);

    @GET("api/likes/user/{userId}")
    Call<List<Like>> getUserLikes(@Path("userId") Long userId);

//    @GET("api/history/user/{userId}")
//    Call<List<CheckWatched>> getUserHistory(@Path("userId") Long userId);

    @GET("api/likes/user/{userId}/episodes")
    Call<Map<Long, String>> getLikedEpisodes(@Path("userId") Long userId);

    @GET("api/likes/user/{userId}/episodes/{episodeId}")
    Call<List<String>> getLikedVideosByEpisode(@Path("userId") Long userId, @Path("episodeId") Long episodeId);

    @GET("api/history/user/{userId}")
    Call<List<CheckWatchedResponse>> getUserHistory(@Path("userId") Long userId);

}


