package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.text.Editable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;

public class SignUp02Activity extends AppCompatActivity {

    private UserData userData; // ✅ 전달받은 객체를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_02);

        // ✅ 전달받은 UserData 객체
        userData = (UserData) getIntent().getSerializableExtra("userData");

        // 버튼 닉네임
        EditText editTextNickname = findViewById(R.id.nickname_hint);
        Button buttonNext = findViewById(R.id.btn_next);

        // 처음에는 버튼을 비활성화
        buttonNext.setEnabled(false);

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        editTextNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 닉네임이 2글자 이상일 때만 버튼 활성화
                buttonNext.setEnabled(s.length() >= 2);
                // 버튼 배경 바꾸고 싶으면 아래처럼 추가로 스타일링 가능
                // buttonNext.setAlpha(s.length() >= 2 ? 1.0f : 0.5f);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = editTextNickname.getText().toString().trim();

                // ✅ userData에 닉네임 저장
                if (userData != null) {
                    userData.nickname = nickname;
                }

                Intent intent = new Intent(SignUp02Activity.this, SignUp03Activity.class);
                intent.putExtra("userData", userData);
                startActivity(intent);
                // 추후 DB 저장 등 추가 작업
                // Intent로 데이터 전달 가능
            }
        });


    }
}
