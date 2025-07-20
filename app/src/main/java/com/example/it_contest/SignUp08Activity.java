package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;

public class SignUp08Activity extends AppCompatActivity {
    private int value = 0;
    private TextView numLeft, numRight;
    private UserData userData; // ✅ 전달받을 UserData

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_08);

        // ✅ UserData 받아오기
        userData = (UserData) getIntent().getSerializableExtra("userData");

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        // 숫자 게이지 연결
        numLeft = findViewById(R.id.num_left);
        numRight = findViewById(R.id.num_right);
        ImageButton btnUp = findViewById(R.id.btn_up);
        ImageButton btnDown = findViewById(R.id.btn_down);

        updateNumber();

        btnUp.setOnClickListener(v -> {
            value = (value + 1) % 100; // 0~99
            updateNumber();
        });

        btnDown.setOnClickListener(v -> {
            value = (value - 1 + 100) % 100; // 음수 방지
            updateNumber();
        });

        // 다음 버튼
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            // ✅ 값 저장
            if (userData != null) {
                userData.caffeine = value;
            }

            // ✅ 다음 액티비티로 전달
            Intent intent = new Intent(SignUp08Activity.this, SignUp09Activity.class);
            intent.putExtra("userData", userData);
            startActivity(intent);
        });
    }

    private void updateNumber() {
        int left = value / 10;
        int right = value % 10;
        numLeft.setText(String.valueOf(left));
        numRight.setText(String.valueOf(right));
    }
}
