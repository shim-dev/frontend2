
package com.example.it_contest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;


import androidx.core.content.ContextCompat;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeVerificationActivity extends BaseActivity {

    private TextView point;
    private Button confirmButton;
    private GridLayout dayGrid;
    private ImageButton backButton;

    private String challengeId;
    private int todayDayCount = 1;
    private int goalSteps = 5000;  // 기본값

    private boolean isTodayCertified = false;
    private Set<Integer> certifiedDays = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_verification);

        setupBottomNavigation("challenge");

        LinearLayout pointContainer = findViewById(R.id.point_container);
        pointContainer.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengeVerificationActivity.this, PointMainActivity.class);
            startActivity(intent);
        });

        point = findViewById(R.id.point);
        confirmButton = findViewById(R.id.confirm_button);
        dayGrid = findViewById(R.id.dayGrid);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        challengeId = getIntent().getStringExtra("challenge_id");
        if (challengeId == null) {
            Toast.makeText(this, "챌린지 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadChallengeDetail(challengeId);

        confirmButton.setOnClickListener(v -> {
            if (certifiedDays.size() == 28 && confirmButton.getText().toString().equals("포인트 받기")) {
                // 보상 지급 처리 (기존 그대로 유지)
                JsonObject request = new JsonObject();
                request.addProperty("nickname", getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("nickname", ""));
                request.addProperty("challenge_id", challengeId);

                ApiService apiService = RetrofitClient.getApiService();
                apiService.giveChallengeReward(request).enqueue(new Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ChallengeVerificationActivity.this, "보상이 지급되었습니다!", Toast.LENGTH_SHORT).show();
                            confirmButton.setEnabled(false);
                            confirmButton.setText("포인트 지급 완료");
                        } else {
                            Toast.makeText(ChallengeVerificationActivity.this, "보상 지급 실패: 이미 받은 경우일 수 있어요.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                        Toast.makeText(ChallengeVerificationActivity.this, "서버 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            if (isTodayCertified || confirmButton.getText().toString().equals("포인트 지급 완료")) {
                Toast.makeText(this, "이미 오늘 인증하셨습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 이미지 업로드 화면으로 이동
            Intent intent = new Intent(ChallengeVerificationActivity.this, ImageUploadActivity.class);
            intent.putExtra("challenge_id", challengeId);
            intent.putExtra("today_day", todayDayCount);
            intent.putExtra("goal_steps", goalSteps);
            startActivityForResult(intent, 1001);
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        loadChallengeDetail(challengeId);  // 인증 성공 후 돌아왔을 때 갱신
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("verified", false)) {
                // ✅ 인증 성공 처리 (예: UI 갱신)
                refreshVerificationUI();  // ← 인증 버튼 색, 상태 변경 등
            }
        }
    }
    private void refreshVerificationUI() {
        updateConfirmButton();
        setupDayButtons();
    }



    private void loadChallengeDetail(String challengeId) {
        ApiService apiService = RetrofitClient.getApiService();
        String nickname = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("nickname", "");
        apiService.getChallengeVerificationStatus(challengeId, nickname).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject responseBody = response.body();
                    String title = responseBody.get("title").getAsString();


                    TextView subtitle = findViewById(R.id.subtitle);
                    subtitle.setText(title);

                    // start_date 가져오기
                    String startDate = responseBody.has("start_date") ? responseBody.get("start_date").getAsString() : null;

                    if (startDate != null) {
                        todayDayCount = calculateDayCount(startDate);
                    } else {
                        todayDayCount = 1;  // fallback
                    }

                    JsonArray arr = responseBody.getAsJsonArray("certified_days");

                    certifiedDays.clear();
                    for (JsonElement e : arr) {
                        try {
                            certifiedDays.add(Integer.parseInt(e.getAsString()));
                        } catch (NumberFormatException ex) {
                            Log.e("ChallengeUI", "숫자 변환 실패: " + e.toString());
                        }
                    }
                    isTodayCertified = certifiedDays.contains(todayDayCount);
                    updateConfirmButton();
                    dayGrid.post(() -> setupDayButtons());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ChallengeVerificationActivity.this, "인증 상태 가져오기 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int calculateDayCount(String startDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(startDateStr);
            Date today = new Date();
            long diff = today.getTime() - startDate.getTime();
            int days = (int) TimeUnit.MILLISECONDS.toDays(diff) + 1;
            return Math.min(Math.max(days, 1), 28);
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private void setupDayButtons() {
        dayGrid.removeAllViews();


        int columns = 4;
        int spacing = (int) (getResources().getDisplayMetrics().density * 12);
        int screenWidth = dayGrid.getWidth();
        int buttonSize = (screenWidth - (spacing * (columns + 1))) / columns;

        for (int i = 1; i <= 28; i++) {
            TextView dayButton = new TextView(this);
            dayButton.setText(i + "일차");
            dayButton.setGravity(Gravity.CENTER);
            dayButton.setTextSize(14);
            dayButton.setTextColor(getResources().getColor(android.R.color.black));

            Drawable bg;
            if (i < todayDayCount) {
                if (certifiedDays.contains(i)) {
                    bg = ContextCompat.getDrawable(this, R.drawable.filled_circle_black);
                    dayButton.setTextColor(Color.WHITE); // 인증 완료는 흰 글씨
                } else {
                    bg = ContextCompat.getDrawable(this, R.drawable.dotted_circle_gray);
                    dayButton.setTextColor(Color.BLACK); // 인증 실패는 검정 글씨
                }
            } else if (i == todayDayCount) {
                if (isTodayCertified) {
                    bg = ContextCompat.getDrawable(this, R.drawable.filled_circle_black);
                    dayButton.setTextColor(Color.WHITE);
                } else {
                    bg = ContextCompat.getDrawable(this, R.drawable.dotted_circle_black);
                    dayButton.setTextColor(Color.BLACK);
                }
            } else {
                bg = ContextCompat.getDrawable(this, R.drawable.dotted_circle_black);
                dayButton.setTextColor(Color.BLACK);
            }

            dayButton.setBackground(bg);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = buttonSize;
            params.height = buttonSize;
            params.setMargins(spacing / 2, spacing / 2, spacing / 2, spacing / 2);
            dayButton.setLayoutParams(params);

            dayGrid.addView(dayButton);
            Log.d("ChallengeUI", "certifiedDays: " + certifiedDays + ", today: " + todayDayCount);

        }
    }

    private void updateConfirmButton() {
        if (confirmButton.getText().toString().equals("포인트 지급 완료")) {
            confirmButton.setEnabled(false);
            return;
        }

        confirmButton.setEnabled(!isTodayCertified);
        confirmButton.setText(isTodayCertified ? "오늘 인증 완료" : "인증하기");
    }

}
