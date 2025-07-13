package com.example.it_contest;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignUp07Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_07);

        SleepGaugeView sleepGaugeView = findViewById(R.id.sleep_gauge);
        TextView sleepHourText = findViewById(R.id.sleep_hour_text);

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        // --- 1. 게이지 값 바뀔 때마다 텍스트 갱신 ---
        sleepGaugeView.setOnSleepValueChangedListener(new SleepGaugeView.OnSleepValueChangedListener() {
            @Override
            public void onValueChanged(int hour) {
                sleepHourText.setText(hour + " hr");
            }
        });

        // --- 2. 숫자 텍스트 클릭시 직접 입력 다이얼로그 ---
        sleepHourText.setOnClickListener(v -> {
            EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setHint("4 ~ 12");

            new AlertDialog.Builder(this)
                    .setTitle("수면 시간 직접 입력")
                    .setView(input)
                    .setPositiveButton("확인", (dialog, which) -> {
                        try {
                            int hour = Integer.parseInt(input.getText().toString());
                            if (hour < 4) hour = 4;
                            if (hour > 12) hour = 12;
                            // SleepGaugeView 값 갱신
                            float progress = (hour - 4) / 8f;
                            sleepGaugeView.setProgress(progress);
                            sleepHourText.setText(hour + " hr");
                        } catch (Exception e) {
                            // 숫자 이외 입력시 무시
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

        // --- 3. 초기값 세팅 ---
        sleepHourText.setText("8 hr");

        // --- 4. 다음 버튼 이동 ---
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp07Activity.this, SignUp08Activity.class);
            startActivity(intent);
        });
    }
}
