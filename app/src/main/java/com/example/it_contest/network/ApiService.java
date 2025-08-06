package com.example.it_contest.network;

import com.example.it_contest.model.Recipe;
import com.example.it_contest.model.SearchHistory;
import com.example.it_contest.model.UserData;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
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

    @GET("/recipes/search")
    Call<List<Recipe>> searchRecipes(
            @Query("keyword") String keyword,
            @Query("sort") String sort
    );

    @POST("/recipes/view/{id}")
    Call<Void> increaseRecipeView(@Path("id") String recipeId);

    // ✅ 검색 기록 추가
    @POST("/search-history/add")
    Call<Void> addSearchHistory(@Body Map<String, String> keyword);

    // ✅ 검색 기록 불러오기
    @GET("/search-history/list")
    Call<List<SearchHistory>> getSearchHistory();

    // ✅ 특정 검색 기록 삭제
    @DELETE("/search-history/delete")
    Call<Void> deleteSearchHistory(@Query("keyword") String keyword);

    // ✅ 전체 검색 기록 삭제
    @DELETE("/search-history/clear")
    Call<Void> clearSearchHistory();

}
