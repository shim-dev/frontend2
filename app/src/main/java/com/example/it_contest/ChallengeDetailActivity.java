package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChallengeDetailActivity extends BaseActivity {

    private ImageButton backButton;
    private LinearLayout pointBox;
    private Button participateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_detail);

        // 하단바 설정
        setupBottomNavigation("challenge");

        // 뷰 초기화
        backButton = findViewById(R.id.back_button);
        pointBox = findViewById(R.id.point_box);
        participateButton = findViewById(R.id.participate_button);

        // 포인트 클릭 시 이동
        pointBox.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengeDetailActivity.this, PointMainActivity.class);
            startActivity(intent);
        });

        // 뒤로가기 버튼
        backButton.setOnClickListener(v -> finish());

        // 참여 버튼
        participateButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengeDetailActivity.this, ChallengePartActivity.class);
            startActivity(intent);
        });
    }
}
