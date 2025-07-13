package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp06Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_06);

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        // 라디오 그룹, 버튼 연결
        RadioGroup radioGroup = findViewById(R.id.week_frequency_group);
        Button btnNext = findViewById(R.id.btn_next);

        // 버튼 초기에는 비활성화
        btnNext.setEnabled(false);

        // 라디오 버튼 선택 감지 → 하나라도 선택되면 버튼 활성화
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                btnNext.setEnabled(checkedId != -1);
            }
        });

        // 다음 버튼 클릭 시 다음 액티비티로 이동
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp06Activity.this, SignUp07Activity.class);
                startActivity(intent);
            }
        });
    }
}
