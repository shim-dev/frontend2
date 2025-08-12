package com.example.it_contest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_main);

        setupBottomNavigation("my");

        TextView greetingTitle = findViewById(R.id.greeting_title);
        TextView greetingPoint = findViewById(R.id.greeting_point);
        TextView pointText = findViewById(R.id.point);

        ApiService apiService = RetrofitClient.getApiService();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);

        if (nickname == null) {
            Log.e("POINT", "로그인 정보 없음");
            // 필요시 로그인 화면으로 이동
            Intent intent = new Intent(PointMainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Call<JsonObject> call = apiService.getUserInfo(nickname);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject data = response.body();
                    String nickname = data.get("nickname").getAsString();
                    int point = data.get("point").getAsInt();

                    // UI에 반영
                    greetingTitle.setText(nickname + "님, 안녕하세요!");
                    greetingPoint.setText("현재 보유 포인트는 " + point + "P 입니다.");
                    pointText.setText(point + " P");
                } else {
                    // 실패 메시지 처리
                    Log.e("POINT", "응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("POINT", "서버 연결 실패", t);
            }
        });


        ImageButton backButton = findViewById(R.id.back_button);


        LinearLayout menuBox = findViewById(R.id.menu_box);
        LinearLayout historyLayout = (LinearLayout) menuBox.getChildAt(0);
        LinearLayout chargeLayout = (LinearLayout) menuBox.getChildAt(2);
        LinearLayout refundLayout = (LinearLayout) menuBox.getChildAt(4);



        backButton.setOnClickListener(v -> onBackPressed());

        historyLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PointMainActivity.this, PointHistoryActivity.class);
            startActivity(intent);
        });

        chargeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PointMainActivity.this, PointChargeActivity.class);
            startActivity(intent);
        });

        refundLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PointMainActivity.this, PointRefundActivity.class);
            startActivity(intent);
        });
    }
}