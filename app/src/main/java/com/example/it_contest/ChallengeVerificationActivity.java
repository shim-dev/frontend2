package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChallengeVerificationActivity extends BaseActivity {

    private TextView point;
    private Button confirmButton;
    private GridLayout dayGrid;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_verification);

        // 하단바 세팅
        setupBottomNavigation("challenge");

        LinearLayout pointContainer = findViewById(R.id.point_container);
        pointContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChallengeVerificationActivity.this, PointMainActivity.class);
                startActivity(intent);
            }
        });

        point = findViewById(R.id.point);
        confirmButton = findViewById(R.id.confirm_button);
        dayGrid = findViewById(R.id.dayGrid);
        backButton = findViewById(R.id.back_button);

        // 뒤로가기 버튼 동작
        backButton.setOnClickListener(v -> finish());

        // GridLayout 버튼 28개 동적 생성
        dayGrid.post(() -> {
            int screenWidth = dayGrid.getWidth();  // GridLayout 가로 길이
            int columns = 4;                       // 한 줄 4개
            int spacing = (int) (getResources().getDisplayMetrics().density * 12); // 버튼 간격
            int buttonSize = (screenWidth - (spacing * (columns + 1))) / columns; // 자동 크기 계산

            for (int i = 1; i <= 28; i++) {
                TextView dayButton = new TextView(this);
                dayButton.setText(i + "일차");
                dayButton.setGravity(Gravity.CENTER);
                dayButton.setTextSize(14);
                dayButton.setTextColor(getResources().getColor(android.R.color.black));
                dayButton.setBackgroundResource(R.drawable.dotted_circle);  // 점선 원 배경

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = buttonSize;
                params.height = buttonSize;
                params.setMargins(spacing / 2, spacing / 2, spacing / 2, spacing / 2);
                dayButton.setLayoutParams(params);

                // 버튼 클릭 시 토스트 표시
                dayButton.setOnClickListener(v ->
                        Toast.makeText(this, dayButton.getText() + " 선택됨", Toast.LENGTH_SHORT).show()
                );

                dayGrid.addView(dayButton);
            }
        });

        // 인증하기 버튼 클릭 동작 (포인트 증가 예시)
        confirmButton.setOnClickListener(v -> {
            Toast.makeText(this, "인증 완료!", Toast.LENGTH_SHORT).show();
            point.setText("750 P");
        });
    }
}
