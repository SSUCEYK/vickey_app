package com.example.vickey.api;

import com.example.vickey.api.dto.CheckWatchedResponse;
import com.example.vickey.api.dto.LikedVideosResponse;
import com.example.vickey.api.dto.LoginRequest;
import com.example.vickey.api.dto.LoginResponse;
import com.example.vickey.api.dto.SignupRequest;
import com.example.vickey.api.dto.SocialLoginRequest;
import com.example.vickey.api.models.Episode;
import com.example.vickey.api.models.Like;
import com.example.vickey.api.models.User;
import com.example.vickey.signup.UserStatus;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Episode
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

    // 에피소드 검색
    @GET("/api/episodes/search")
    Call<List<Episode>> searchEpisodes(@Query("searchQuery") String searchQuery);

    // 에피소드 상세 정보
    @GET("api/episodes/contentInfo")
    Call<Episode> contentInfoEpisodes(@Query("contentInfoQuery") Long contentInfoQuery);

    // ID 리스트로 title 리스트 가져오기
    @POST("api/getEpisodeTitles")
    Call<List<String>> getEpisodeTitles(@Body List<Long> episodeIds);


    // Like, History
    @GET("api/likes/user/{userId}")
    Call<List<Like>> getUserLikes(@Path("userId") String userId);

    @GET("api/likes/user/{userId}/episodes")
    Call<List<Episode>> getLikedEpisodes(@Path("userId") String userId);

    @GET("api/likes/user/{userId}/episodes/{episodeId}")
    Call<List<LikedVideosResponse>> getLikedVideosByEpisode(@Path("userId") String userId, @Path("episodeId") Long episodeId);

    @GET("api/history/user/{userId}")
    Call<List<CheckWatchedResponse>> getUserHistory(@Path("userId") String userId);


    // User
    // 이메일 로그인
    @POST("api/users/email-login")
    Call<LoginResponse> loginWithEmail(@Body LoginRequest loginRequest);

    // 이메일 회원가입
    @POST("api/users/email-signup")
    Call<LoginResponse> signupWithEmail(@Body SignupRequest signupRequest);

    // 소셜 로그인 (카카오/네이버)
    @POST("api/users/social-login")
    Call<LoginResponse> socialLogin(@Body SocialLoginRequest socialLoginRequest);

    @POST("api/users/register") // Spring 서버의 엔드포인트
    Call<Void> registerUser(@Body User user); // User 객체 전송

    @GET("api/users/status")
    Call<UserStatus> getUserStatus(@Query("uid") String uid);
}


