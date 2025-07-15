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

import com.example.it_contest.model.UserData;

public class SignUp07Activity extends AppCompatActivity {

    private UserData userData;
    private int currentSleepHour = 8; // 초기값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_07);

        // ✅ 이전 화면에서 UserData 전달받기
        userData = (UserData) getIntent().getSerializableExtra("userData");

        SleepGaugeView sleepGaugeView = findViewById(R.id.sleep_gauge);
        TextView sleepHourText = findViewById(R.id.sleep_hour_text);

        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        // 1. 게이지 값 → 텍스트 갱신
        sleepGaugeView.setOnSleepValueChangedListener(hour -> {
            currentSleepHour = hour;
            sleepHourText.setText(hour + " hr");
        });

        // 2. 텍스트 클릭 → 직접 입력
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
                            float progress = (hour - 4) / 8f;
                            sleepGaugeView.setProgress(progress);
                            currentSleepHour = hour;
                            sleepHourText.setText(hour + " hr");
                        } catch (Exception e) {
                            // 무시
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

        // 3. 초기 텍스트 설정
        sleepHourText.setText("8 hr");

        // 4. 다음 버튼 → 수면시간 저장 후 이동
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            // ✅ 수면시간 저장
            if (userData != null) {
                userData.sleepHours = currentSleepHour;
            }

            // ✅ 다음 화면으로 전달
            Intent intent = new Intent(SignUp07Activity.this, SignUp08Activity.class);
            intent.putExtra("userData", userData);
            startActivity(intent);
        });
    }
}
