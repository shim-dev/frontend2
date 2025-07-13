package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp04Activity extends AppCompatActivity {
    private boolean isAnimating = false; // 한 번에 하나만 동작

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_04); // 레이아웃 파일명 맞게!

        // 1. 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        setupGaugeButton(
                findViewById(R.id.frame_male),
                findViewById(R.id.gauge_male),
                findViewById(R.id.text_male)
        );
        setupGaugeButton(
                findViewById(R.id.frame_female),
                findViewById(R.id.gauge_female),
                findViewById(R.id.text_female)
        );
        setupGaugeButton(
                findViewById(R.id.frame_etc),
                findViewById(R.id.gauge_etc),
                findViewById(R.id.text_etc)
        );
    }

    private void setupGaugeButton(FrameLayout frame, View gauge, TextView text) {
        Handler handler = new Handler();
        final int duration = 1000; // 밀리초 (1초)
        final int interval = 10;   // 게이지 애니메이션 간격(ms)
        final int[] maxWidth = {0};
        final boolean[] isPressed = {false};

        // 텍스트뷰의 Touch 이벤트 사용
        text.setOnTouchListener(new View.OnTouchListener() {
            long startTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isAnimating) return true; // 다른 버튼 누르는 중엔 무시

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed[0] = true;
                        startTime = System.currentTimeMillis();

                        // 게이지 초기화
                        gauge.setLayoutParams(new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT));
                        gauge.setVisibility(View.VISIBLE);

                        // 최대 너비 측정
                        frame.post(() -> {
                            maxWidth[0] = frame.getWidth();
                            handler.post(updateRunnable);
                        });
                        return true;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isPressed[0] = false;
                        // 완전히 차지 않았으면 게이지 리셋
                        gauge.setLayoutParams(new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT));
                        return true;
                }
                return false;
            }

            Runnable updateRunnable = new Runnable() {
                @Override
                public void run() {
                    if (!isPressed[0]) return;
                    long elapsed = System.currentTimeMillis() - startTime;
                    float ratio = Math.min(1f, (float) elapsed / duration);
                    int width = (int) (maxWidth[0] * ratio);
                    gauge.setLayoutParams(new FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.MATCH_PARENT));

                    if (ratio < 1f) {
                        handler.postDelayed(this, interval);
                    } else {
                        // 게이지 완료!
                        isAnimating = true;
                        gauge.setLayoutParams(new FrameLayout.LayoutParams(maxWidth[0], FrameLayout.LayoutParams.MATCH_PARENT));
                        // 다음 화면 이동
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(SignUp04Activity.this, SignUp05Activity.class);
                            startActivity(intent);
                            finish();
                        }, 200); // 0.2초 후 화면 전환 (게이지 꽉 찬 효과 보이게)
                    }
                }
            };
        });
    }
}

