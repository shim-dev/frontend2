package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.Editable;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;

public class SignUp02Activity extends AppCompatActivity {

    private UserData userData; // ✅ 전달받은 객체를 저장할 변수
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp02Activity extends AppCompatActivity {

    private UserData userData;
    private EditText editTextNickname;
    private Button buttonNext;
    private boolean isNicknameValid = false; // 닉네임 중복 확인 결과 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_02);

        // ✅ 전달받은 UserData 객체
        userData = (UserData) getIntent().getSerializableExtra("userData");

        // 버튼 닉네임
        EditText editTextNickname = findViewById(R.id.nickname_hint);
        Button buttonNext = findViewById(R.id.btn_next);
        userData = (UserData) getIntent().getSerializableExtra("userData");
        editTextNickname = findViewById(R.id.nickname_hint);
        buttonNext = findViewById(R.id.btn_next);
        buttonNext.setEnabled(false);

        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        editTextNickname.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 닉네임이 2글자 이상일 때만 버튼 활성화
                buttonNext.setEnabled(s.length() >= 2);
                // 버튼 배경 바꾸고 싶으면 아래처럼 추가로 스타일링 가능
                // buttonNext.setAlpha(s.length() >= 2 ? 1.0f : 0.5f);

                // 이름이 2글자 미만일 경우 에러 메시지 표시
                if (s.length() < 2) {
                    editTextNickname.setError("이름은 2자 이상이어야 합니다.");
                } else {
                    editTextNickname.setError(null); // 에러 해제
                }
            public void afterTextChanged(Editable s) {
                String nickname = s.toString().trim();

                if (nickname.length() < 2) {
                    editTextNickname.setError("이름은 2자 이상이어야 합니다.");
                    buttonNext.setEnabled(false);
                    return;
                }

                // 닉네임 중복 확인
                checkNicknameDuplication(nickname);
            }
        });

        buttonNext.setOnClickListener(v -> {
            String nickname = editTextNickname.getText().toString().trim();
            if (userData != null) userData.nickname = nickname;

            Intent intent = new Intent(SignUp02Activity.this, SignUp03Activity.class);
            intent.putExtra("userData", userData);
            startActivity(intent);
        });
    }

    private void checkNicknameDuplication(String nickname) {
        JsonObject json = new JsonObject();
        json.addProperty("nickname", nickname);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.checkNickname(json).enqueue(new Callback<JsonObject>() {
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
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exists = response.body().get("exists").getAsBoolean();
                    if (exists) {
                        editTextNickname.setError("이미 사용 중인 닉네임입니다.");
                        isNicknameValid = false;
                        buttonNext.setEnabled(false);
                    } else {
                        editTextNickname.setError(null);
                        isNicknameValid = true;
                        buttonNext.setEnabled(true);
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                editTextNickname.setError("서버 오류: 중복 확인 실패");
                isNicknameValid = false;
                buttonNext.setEnabled(false);
            }
        });
    }
}
