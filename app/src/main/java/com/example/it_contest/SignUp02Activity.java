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
