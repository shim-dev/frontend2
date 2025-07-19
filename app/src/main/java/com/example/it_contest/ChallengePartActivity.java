package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ChallengePartActivity extends BaseActivity {

    private ImageButton backButton;
    private Button confirmButton;
    private TextView pointText;
    private LinearLayout pointContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_part);

        // 하단 네비게이션 설정
        setupBottomNavigation("challenge");

        // View 연결
        backButton = findViewById(R.id.back_button);
        confirmButton = findViewById(R.id.confirm_button);
        pointText = findViewById(R.id.point);
        pointContainer = findViewById(R.id.point_container);  // 상단 포인트 영역 전체

        // 포인트 영역 클릭 시 포인트 메인 페이지로 이동
        pointContainer.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengePartActivity.this, PointMainActivity.class);
            startActivity(intent);
        });

        // 뒤로가기 버튼 동작
        backButton.setOnClickListener(v -> onBackPressed());

        // 참가 버튼 클릭 동작
        confirmButton.setOnClickListener(v -> {
            Toast.makeText(ChallengePartActivity.this, "챌린지 참가가 완료되었습니다!", Toast.LENGTH_SHORT).show();

            // TODO: 서버 전송 로직 또는 다른 액티비티로 이동
            // 예: finish();  // 현재 액티비티 닫기
        });
    }
}
