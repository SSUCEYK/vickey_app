package com.example.vickey;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import java.util.List;


//APIService 관련
public interface APIService {
    @GET("/api/episodes/search")
    Call<List<Episode>> searchEpisodes(@Query("searchQuery") String searchQuery);

    @GET("api/episodes/contentInfo")
    Call<Episode> contentInfoEpisodes(@Query("contentInfoQuery") Integer contentInfoQuery);

    @POST("/api/users/register") // Spring 서버의 엔드포인트
    Call<Void> registerUser(@Body User user); // User 객체 전송

    @GET("api/users/status")
    Call<UserStatus> getUserStatus(@Query("uid") String uid);
}