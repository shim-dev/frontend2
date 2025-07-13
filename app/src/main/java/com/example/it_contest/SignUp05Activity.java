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

public class SignUp05Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_05);

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        // 1. EditText, Button 찾기
        EditText editHeight = findViewById(R.id.edit_height);
        EditText editWeight = findViewById(R.id.edit_weight);
        Button btnNext = findViewById(R.id.btn_next);

        // 2. 처음엔 버튼 비활성화
        btnNext.setEnabled(false);

        // 3. TextWatcher 만들기
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 두 입력칸 모두 값이 있으면 버튼 활성화
                String height = editHeight.getText().toString().trim();
                String weight = editWeight.getText().toString().trim();

                btnNext.setEnabled(!height.isEmpty() && !weight.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        // 4. 두 입력칸에 TextWatcher 연결
        editHeight.addTextChangedListener(watcher);
        editWeight.addTextChangedListener(watcher);

        // 5. 다음 버튼 클릭 시 화면 전환
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp05Activity.this, SignUp06Activity.class);
                startActivity(intent);
            }
        });
    }
}
