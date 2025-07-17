package com.example.it_contest.network;

public interface ApiCallback {
    void onSuccess();
    void onError(Throwable t);
}
