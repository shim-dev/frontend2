package com.example.it_contest.network;

import com.example.it_contest.model.Recipe;
import com.example.it_contest.model.UserData;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/register")
    Call<Void> registerUser(@Body UserData userData);

    @POST("/login")
    Call<JsonObject> loginUser(@Body JsonObject body);

    @POST("/check-email")
    Call<JsonObject> checkEmail(@Body JsonObject email);

    @POST("/check-nickname")
    Call<JsonObject> checkNickname(@Body JsonObject nickname);

    @GET("/keywords")
    Call<Map<String, Object>> getKeywords();

    @GET("/recipes/search")
    Call<List<Recipe>> searchRecipes(@Query("keyword") String keyword);

}
