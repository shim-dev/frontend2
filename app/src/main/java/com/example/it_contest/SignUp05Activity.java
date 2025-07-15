package com.example.it_contest;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;

public class SignUp05Activity extends AppCompatActivity {
    private UserData userData; // ✅ 전달받은 객체 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_05);

        // ✅ 전달받기
        userData = (UserData) getIntent().getSerializableExtra("userData");

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        EditText editHeight = findViewById(R.id.edit_height);
        EditText editWeight = findViewById(R.id.edit_weight);
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String height = editHeight.getText().toString().trim();
                String weight = editWeight.getText().toString().trim();
                btnNext.setEnabled(!height.isEmpty() && !weight.isEmpty());
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        editHeight.addTextChangedListener(watcher);
        editWeight.addTextChangedListener(watcher);

        btnNext.setOnClickListener(v -> {
            // ✅ 입력값 저장
            String heightStr = editHeight.getText().toString().trim();
            String weightStr = editWeight.getText().toString().trim();

            try {
                userData.heightCm = Integer.parseInt(heightStr);
                userData.weightKg = Integer.parseInt(weightStr);
            } catch (NumberFormatException e) {
                // 에러 발생 시 무시 (이미 유효성 검사를 통해 비어있지 않은 값만 들어오므로 거의 발생 안 함)
            }

            // ✅ 다음 화면으로 전달
            Intent intent = new Intent(SignUp05Activity.this, SignUp06Activity.class);
            intent.putExtra("userData", userData);
            startActivity(intent);
        });
    }
}
