package com.example.vickey;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;


//APIService 관련
public interface APIService {
    @GET("/api/episodes/search")
    Call<List<Episode>> searchEpisodes(@Query("query") String query);
}