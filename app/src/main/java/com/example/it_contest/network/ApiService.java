package com.example.it_contest.network;

import com.example.it_contest.model.UserData;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;
import com.google.gson.JsonArray;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/register")
    Call<Void> registerUser(@Body UserData userData);

    @POST("/login")
    Call<JsonObject> loginUser(@Body JsonObject body);


    // 챌린지 목록 불러오기 (filter: "all" 또는 "my")
    @GET("/api/challenges")
    Call<JsonElement> getChallenges(@Query("filter") String filter, @Query("nickname") String nickname);


    // 챌린지 참여하기
    @POST("/api/challenges/join")
    Call<JsonObject> participateChallenge(@Body JsonObject body);




    // 챌린지 상세 조회
    @GET("/api/challenges/{id}")
    Call<JsonObject> getChallengeDetail(@Path("id") String challengeId);

    // 챌린지 인증하기
    @GET("/api/challenges/{id}/verification")
    Call<JsonArray> getVerificationStatus(@Path("id") String challengeId, @Query("nickname") String nickname);

    @POST("/api/challenges/{id}/verification")
    Call<JsonObject> postVerification(@Path("id") String challengeId, @Body JsonObject body);

    // 닉네임 + 포인트 조회
    @GET("/api/user-info")
    Call<JsonObject> getUserInfo(@Query("nickname") String nickname);


    @GET("/api/points/history/{nickname}")
    Call<JsonArray> getPointHistory(@Path("nickname") String nickname);

    @POST("/api/refund")
    Call<JsonObject> applyRefund(@Body JsonObject body);

    @POST("/api/challenges/reward")
    Call<ResponseBody> giveChallengeReward(@Body JsonObject requestData);

    @POST("/api/challenges/verify")
    Call<JsonObject> verifyChallengeImage(@Body JsonObject body);

    @GET("/api/challenge_verification/status")
    Call<JsonObject> getChallengeVerificationStatus(
            @Query("challenge_id") String challengeId,
            @Query("nickname") String nickname
    );



}
