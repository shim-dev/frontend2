package com.example.it_contest;


import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmail, editPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // login.xml과 연결

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        // 로그인 관련 뷰 연결
        editEmail = findViewById(R.id.edit_id);
        editPassword = findViewById(R.id.edit_pw);
        btnLogin = findViewById(R.id.btn_login);

        // 로그인 버튼 클릭 이벤트
        btnLogin.setOnClickListener(v -> attemptLogin());
      
        // 비밀번호 재설정 클릭 이벤트
        TextView findPw = findViewById(R.id.find_pw);
        findPw.setOnClickListener(v -> showEmailPopup());
    }

    private void attemptLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        ApiService apiService = RetrofitClient.getApiService();
        Call<JsonObject> call = apiService.loginUser(json);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    String nickname = body.get("nickname").getAsString();
                    String email = body.get("email").getAsString();

                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    prefs.edit().putString("email", email).putString("nickname", nickname).apply();

                    Log.d("LoginActivity", "로그인 성공");
                    // 다음 화면으로 이동
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "로그인 실패: 이메일 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmailPopup() {
        // Dialog 객체 생성
        final Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.email_popup);

        // Dialog 외곽 클릭 시 닫히게 설정
        dialog.setCancelable(true);

        // 이메일 입력창과 전송 버튼 찾기
        EditText editEmail = dialog.findViewById(R.id.edit_email);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit);

        // 전송 버튼 클릭 시 동작
        btnSubmit.setOnClickListener(view -> {
            String email = editEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: 서버에 이메일 전송 요청 넣기
                Toast.makeText(this, "이메일 전송 완료!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show(); // 팝업 띄우기
    }

}
