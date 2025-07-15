package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;

public class SignUp06Activity extends AppCompatActivity {
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_06);

        // ✅ 이전 화면에서 전달된 UserData 받기
        userData = (UserData) getIntent().getSerializableExtra("userData");

        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        RadioGroup radioGroup = findViewById(R.id.week_frequency_group);
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setEnabled(false);

        // 선택되면 버튼 활성화
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            btnNext.setEnabled(checkedId != -1);
        });

        // 다음 버튼 클릭 시 → activityLevel 저장 후 다음 화면으로 전달
        btnNext.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();

            if (userData != null) {
                if (checkedId == R.id.rb_week_1_or_less) {
                    userData.activityLevel = "주 1회 이하";
                } else if (checkedId == R.id.rb_week_1_2) {
                    userData.activityLevel = "주 1 - 2회";
                } else if (checkedId == R.id.rb_week_3_4) {
                    userData.activityLevel = "주 3 - 4회";
                } else if (checkedId == R.id.rb_week_5_6) {
                    userData.activityLevel = "주 5 - 6회";
                } else if (checkedId == R.id.rb_week_7_or_more) {
                    userData.activityLevel = "주 7회 이상";
                }
            }

            Intent intent = new Intent(SignUp06Activity.this, SignUp07Activity.class);
            intent.putExtra("userData", userData);
            startActivity(intent);
        });
    }
}
