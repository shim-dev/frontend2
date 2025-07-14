package com.example.it_contest;

import android.os.Bundle;

public class ChallengeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_main);  // 레이아웃 파일 지정

        setupBottomNavigation("challenge");  // 현재 페이지는 challenge임을 지정
    }
}
