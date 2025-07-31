package com.example.it_contest.network;

import com.example.it_contest.model.UserData;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/register")
    Call<Void> registerUser(@Body UserData userData);

    @POST("/login")
    Call<JsonObject> loginUser(@Body JsonObject body);

    @POST("/check-email")
    Call<JsonObject> checkEmail(@Body JsonObject email);

    @POST("/check-nickname")
    Call<JsonObject> checkNickname(@Body JsonObject nickname);

}
