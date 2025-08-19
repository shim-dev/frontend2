package com.example.it_contest.network;

import com.example.it_contest.model.UserData;
import com.example.it_contest.model.CreateRecipeRequest;
import com.example.it_contest.model.Recipe;
import com.example.it_contest.model.SearchHistory;
import com.example.it_contest.network.DietRecord;
import com.example.it_contest.network.FoodSearchResult;
import com.example.it_contest.network.ChatMealResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
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

    @GET("/me")
    Call<JsonObject> getMe(@Query("email") String email);

    @POST("/check-email")
    Call<JsonObject> checkEmail(@Body JsonObject email);

    @POST("/check-nickname")
    Call<JsonObject> checkNickname(@Body JsonObject nickname);

    @GET("/api/user-info")
    Call<JsonObject> getUserInfo(@Query("nickname") String nickname);


    @GET("/api/challenges")
    Call<JsonElement> getChallenges(@Query("filter") String filter,
                                    @Query("nickname") String nickname);

    @POST("/api/challenges/join")
    Call<JsonObject> participateChallenge(@Body JsonObject body);

    @GET("/api/challenges/{id}")
    Call<JsonObject> getChallengeDetail(@Path("id") String challengeId);

    @GET("/api/challenges/{id}/verification")
    Call<JsonArray> getVerificationStatus(@Path("id") String challengeId,
                                          @Query("nickname") String nickname);

    @POST("/api/challenges/{id}/verification")
    Call<JsonObject> postVerification(@Path("id") String challengeId,
                                      @Body JsonObject body);

    @POST("/api/challenges/reward")
    Call<ResponseBody> giveChallengeReward(@Body JsonObject requestData);

    @POST("/api/challenges/verify")
    Call<JsonObject> verifyChallengeImage(@Body JsonObject body);

    @GET("/api/challenge_verification/status")
    Call<JsonObject> getChallengeVerificationStatus(@Query("challenge_id") String challengeId,
                                                    @Query("nickname") String nickname);
    
    @GET("/api/points/history/{nickname}")
    Call<JsonArray> getPointHistory(@Path("nickname") String nickname);

    @POST("/api/refund")
    Call<JsonObject> applyRefund(@Body JsonObject body);

    @GET("/api/history")
    Call<List<DietRecord>> getHistory(@Query("email") String email);

    @POST("/api/foods/search")
    Call<List<FoodSearchResult>> searchFood(@Body Map<String, String> body);

    @POST("/api/meals/add")
    Call<DietRecord> addMealRecord(@Body JsonObject body);

    @DELETE("/api/meals/delete/{record_id}")
    Call<JsonObject> deleteMealRecord(@Path("record_id") String recordId);

    @GET("/api/meals/list")
    Call<List<DietRecord>> getMealsByDay(@Query("nickname") String nickname,
                                         @Query("meal_type") String mealType,
                                         @Query("date") String yyyymmdd);

    @POST("/api/chat-meal")
    Call<ChatMealResponse> chatMeal(@Body JsonObject body);

    @GET("/keywords")
    Call<Map<String, Object>> getKeywords();

    @GET("/recipes/search")
    Call<List<Recipe>> searchRecipes(@Query("keyword") String keyword,
                                     @Query("sort") String sort);

    @POST("/recipes/view/{id}")
    Call<Void> increaseRecipeView(@Path("id") String recipeId);

    @POST("/posts/recipe")
    Call<JsonObject> createRecipe(@Body CreateRecipeRequest request);

    @POST("/search-history/add")
    Call<Void> addSearchHistory(@Body Map<String, String> keyword);

    @GET("/search-history/list")
    Call<List<SearchHistory>> getSearchHistory();

    @DELETE("/search-history/delete")
    Call<Void> deleteSearchHistory(@Query("keyword") String keyword);

    @DELETE("/search-history/clear")
    Call<Void> clearSearchHistory();
}
