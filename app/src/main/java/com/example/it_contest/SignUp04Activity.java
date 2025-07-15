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

import com.example.it_contest.model.UserData;

public class SignUp04Activity extends AppCompatActivity {
    private boolean isAnimating = false;
    private UserData userData; // ✅ 이전 화면에서 전달받은 데이터

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_04);

        // ✅ UserData 받기
        userData = (UserData) getIntent().getSerializableExtra("userData");

        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        // 각 버튼 연결 + 선택 시 성별 저장
        setupGaugeButton(
                findViewById(R.id.frame_male),
                findViewById(R.id.gauge_male),
                findViewById(R.id.text_male),
                "male"
        );
        setupGaugeButton(
                findViewById(R.id.frame_female),
                findViewById(R.id.gauge_female),
                findViewById(R.id.text_female),
                "female"
        );
        setupGaugeButton(
                findViewById(R.id.frame_etc),
                findViewById(R.id.gauge_etc),
                findViewById(R.id.text_etc),
                "etc"
        );
    }

    // ✅ 성별 값까지 받는 메서드로 수정
    private void setupGaugeButton(FrameLayout frame, View gauge, TextView text, String genderValue) {
        Handler handler = new Handler();
        final int duration = 1000;
        final int interval = 10;
        final int[] maxWidth = {0};
        final boolean[] isPressed = {false};

        text.setOnTouchListener(new View.OnTouchListener() {
            long startTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isAnimating) return true;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed[0] = true;
                        startTime = System.currentTimeMillis();

                        gauge.setLayoutParams(new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT));
                        gauge.setVisibility(View.VISIBLE);

                        frame.post(() -> {
                            maxWidth[0] = frame.getWidth();
                            handler.post(updateRunnable);
                        });
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isPressed[0] = false;
                        gauge.setLayoutParams(new FrameLayout.LayoutParams(0, FrameLayout.LayoutParams.MATCH_PARENT));
                        return true;
                }
                return false;
            }

            // 애니메이션 Runnable
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
                        isAnimating = true;
                        gauge.setLayoutParams(new FrameLayout.LayoutParams(maxWidth[0], FrameLayout.LayoutParams.MATCH_PARENT));

                        // ✅ 성별 저장
                        if (userData != null) {
                            userData.gender = genderValue;
                        }

                        // ✅ 다음 화면으로 전달
                        handler.postDelayed(() -> {
                            Intent intent = new Intent(SignUp04Activity.this, SignUp05Activity.class);
                            intent.putExtra("userData", userData);
                            startActivity(intent);
                            finish();
                        }, 200);
                    }
                }
            };
        });
    }
}
