package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;

import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


public class SignUp09Activity extends AppCompatActivity {

    private int value = 0;
    private TextView numLeft, numRight;
    private UserData userData; // ✅ 전달받을 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_09);

        // ✅ 전달받기
        userData = (UserData) getIntent().getSerializableExtra("userData");

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        // 숫자 게이지
        numLeft = findViewById(R.id.num_left);
        numRight = findViewById(R.id.num_right);
        ImageButton btnUp = findViewById(R.id.btn_up);
        ImageButton btnDown = findViewById(R.id.btn_down);

        updateNumber();

        btnUp.setOnClickListener(v -> {
            value = (value + 1) % 100;
            updateNumber();
        });

        btnDown.setOnClickListener(v -> {
            value = (value - 1 + 100) % 100;
            updateNumber();
        });

        // 다음 버튼
        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            // ✅ 음주량 저장
            if (userData != null) {
                userData.alcohol = value;
            }

            // Flask 서버 URL
            String url = "http://118.32.91.252:5000/register";  // 에뮬레이터 기준. 실제 기기면 IP로 변경

            // JSON으로 변환
            JSONObject json = new JSONObject();
            try {
                json.put("email", userData.email);
                json.put("password", userData.password);
                json.put("nickname", userData.nickname);
                json.put("birthdate", userData.birthdate);
                json.put("gender", userData.gender);
                json.put("heightCm", userData.heightCm);
                json.put("weightKg", userData.weightKg);
                json.put("activityLevel", userData.activityLevel);
                json.put("sleepHours", userData.sleepHours);
                json.put("caffeine", userData.caffeine);
                json.put("alcohol", userData.alcohol);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Volley 요청 보내기
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                    response -> {
                        Log.d("REGISTER", "성공: " + response.toString());

                        // 다음 화면으로 이동
                        Intent intent = new Intent(SignUp09Activity.this, SignUp10Activity.class);
                        intent.putExtra("userData", userData);
                        startActivity(intent);
                    },
                    error -> Log.e("REGISTER", "오류: " + error.toString())
            );

            queue.add(request);


        });
    }

    private void updateNumber() {
        int left = value / 10;
        int right = value % 10;
        numLeft.setText(String.valueOf(left));
        numRight.setText(String.valueOf(right));
    }
}
