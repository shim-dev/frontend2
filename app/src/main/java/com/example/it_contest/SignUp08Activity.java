package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp08Activity extends AppCompatActivity {
    private int value = 0; // 시작값, 원하면 0 등으로 수정!
    private TextView numLeft, numRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_08);

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 숫자 게이지 연결
        numLeft = findViewById(R.id.num_left);
        numRight = findViewById(R.id.num_right);
        ImageButton btnUp = findViewById(R.id.btn_up);
        ImageButton btnDown = findViewById(R.id.btn_down);

        updateNumber();

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = (value + 1) % 100; // 0~99
                updateNumber();
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                value = (value - 1 + 100) % 100;
                updateNumber();
            }
        });

        // 다음 버튼 구현
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp08Activity.this, SignUp09Activity.class);
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
