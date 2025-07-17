package com.example.it_contest.network;

import com.example.it_contest.model.UserData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/register")
    Call<Void> registerUser(@Body UserData userData);
}
