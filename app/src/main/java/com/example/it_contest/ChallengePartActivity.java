package com.example.it_contest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;
import java.text.SimpleDateFormat;
import java.util.Date;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChallengePartActivity extends BaseActivity {

    private ImageButton backButton;
    private Button confirmButton;
    private TextView pointText;
    private LinearLayout pointContainer;
    private String challengeId;
    private ImageView challengeImage;
    private TextView entryFee2View;
    private TextView myPointsView;
    private TextView pointsAfterPaymentView;
    private int entryFee2 = 0; //도전금 계산의 도전금
    private int myPoints = 0;

    // 상세 정보 표시용 UI 요소들
    private TextView challengeTitleText;
    private TextView challengeDateRange;
    private TextView entryFeeText;
    private TextView challengeStatusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);

        if (nickname == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }





        setContentView(R.layout.challenge_part);

        setupBottomNavigation("challenge");
        loadUserPoint(nickname);

        backButton = findViewById(R.id.back_button);
        confirmButton = findViewById(R.id.confirm_button);
        pointText = findViewById(R.id.point);
        pointContainer = findViewById(R.id.point_container);
        challengeImage = findViewById(R.id.challenge_image);

        // 새로 추가한 텍스트뷰 연결
        challengeTitleText = findViewById(R.id.challenge_title_text);
        challengeDateRange = findViewById(R.id.challenge_date_range);
        entryFeeText = findViewById(R.id.entry_fee);
        entryFee2View = findViewById(R.id.entry_fee2);
        myPointsView = findViewById(R.id.my_points);
        pointsAfterPaymentView = findViewById(R.id.points_after_payment);
        // 예시: 유저 포인트를 서버에서 받아오면 설정해야 함.

        myPointsView.setText(myPoints + " P");
        challengeStatusView = findViewById(R.id.challenge_status);
        confirmButton = findViewById(R.id.confirm_button);


        challengeId = getIntent().getStringExtra("challenge_id");

        pointContainer.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengePartActivity.this, PointMainActivity.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> onBackPressed());

        confirmButton.setOnClickListener(v -> {
            participateInChallenge();
        });

        // 챌린지 상세정보 불러오기
        loadChallengeDetail(challengeId);
    }

    private void loadUserPoint(String nickname) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUserInfo(nickname).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myPoints = response.body().get("point").getAsInt();
                    myPointsView.setText(myPoints + " P");

                    // challenge 정보 이미 로드됐으면 포인트 기반 UI 다시 업데이트
                    if (entryFee2 > 0) {
                        int pointsLeft = myPoints - entryFee2;
                        pointsAfterPaymentView.setText(pointsLeft + " P");

                        if (myPoints >= entryFee2) {
                            challengeStatusView.setText("도전 가능");
                            challengeStatusView.setTextColor(Color.parseColor("#82A888"));
                            confirmButton.setEnabled(true);
                        } else {
                            challengeStatusView.setText("도전 불가능");
                            challengeStatusView.setTextColor(Color.parseColor("#FF4444"));
                            confirmButton.setEnabled(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ChallengePartActivity.this, "포인트 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadChallengeDetail(String challengeId) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getChallengeDetail(challengeId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(ChallengePartActivity.this, "상세 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ChallengePartActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(JsonObject challenge) {
        if (challengeImage != null) {
            String imageUrl = challenge.get("image_url").getAsString();
            Glide.with(this).load(imageUrl).into(challengeImage);
        }

        if (challengeTitleText != null)
            challengeTitleText.setText(challenge.get("title").getAsString());

        if (challengeDateRange != null) {
            String start = challenge.get("start_date").getAsString();
            String end = challenge.get("end_date").getAsString();
            challengeDateRange.setText(formatDateRange(start, end));
        }

        if (entryFeeText != null)
            entryFeeText.setText(challenge.get("points_reward").getAsInt() + " P");

        entryFee2 = challenge.get("points_reward").getAsInt();
        entryFee2View.setText(entryFee2 + " P");

        int pointsLeft = myPoints - entryFee2;
        pointsAfterPaymentView.setText(pointsLeft + " P");

        // 도전 가능 여부 체크 및 UI 업데이트
        if (myPoints >= entryFee2) {
            challengeStatusView.setText("도전 가능");
            challengeStatusView.setTextColor(Color.parseColor("#82A888"));
            confirmButton.setEnabled(true);
        } else {
            challengeStatusView.setText("도전 불가능");
            challengeStatusView.setTextColor(Color.parseColor("#FF4444"));
            confirmButton.setEnabled(false);
        }
    }


    // 날짜 범위 포맷팅 메서드 (yyyy-MM-dd → yy.MM.dd)
    private String formatDateRange(String start, String end) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat displayFormat = new SimpleDateFormat("yy.MM.dd");
        try {
            Date startDate = serverFormat.parse(start);
            Date endDate = serverFormat.parse(end);
            return displayFormat.format(startDate) + " ~ " + displayFormat.format(endDate);
        } catch (Exception e) {
            e.printStackTrace();
            return start + " ~ " + end;
        }


    }

    private void participateInChallenge() {
        ApiService apiService = RetrofitClient.getApiService();

        JsonObject body = new JsonObject();
        body.addProperty("challenge_id", challengeId);
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);

        if (nickname == null) {
            Toast.makeText(this, "로그인 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        body.addProperty("nickname", nickname);

        apiService.participateChallenge(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChallengePartActivity.this, "챌린지 참가가 완료되었습니다!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChallengePartActivity.this, "참가 실패: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ChallengePartActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}