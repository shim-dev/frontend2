package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ChallengeDetailActivity extends BaseActivity {

    private ImageButton backButton;
    private LinearLayout pointBox;
    private Button participateButton;

    // 상세 화면 UI 요소 선언
    private ImageView challengeImage;
    private TextView titleView, descriptionView, pointsView, maxParticipantsView, currentParticipantsView ;
    private TextView deadlineView, challengeDateRangeView;
    private String challengeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_detail);

        // 하단바 설정
        setupBottomNavigation("challenge");

        // 뷰 초기화
        backButton = findViewById(R.id.back_button);
        pointBox = findViewById(R.id.point_box);
        participateButton = findViewById(R.id.participate_button);

        challengeImage = findViewById(R.id.challenge_image);
        titleView = findViewById(R.id.challenge_title);
        descriptionView = findViewById(R.id.challenge_description);
        pointsView = findViewById(R.id.challenge_points);
        maxParticipantsView = findViewById(R.id.max_participants_text);
        currentParticipantsView = findViewById(R.id.current_participants_text);
        deadlineView = findViewById(R.id.deadline);
        challengeDateRangeView = findViewById(R.id.challenge_date_range);

        // 포인트 클릭 시 이동
        pointBox.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengeDetailActivity.this, PointMainActivity.class);
            startActivity(intent);
        });

        // 뒤로가기 버튼
        backButton.setOnClickListener(v -> finish());

        // 참여 버튼
        challengeId = getIntent().getStringExtra("challenge_id");
        participateButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengeDetailActivity.this, ChallengePartActivity.class);
            // 챌린지 ID 전달
            intent.putExtra("challenge_id", challengeId);
            startActivity(intent);
        });


        // 인텐트에서 챌린지 ID 받아서 API 호출
        String challengeId = getIntent().getStringExtra("challenge_id");
        loadChallengeDetail(challengeId);
    }

    // 서버에서 상세 정보 받아오는 메서드
    private void loadChallengeDetail(String challengeId) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getChallengeDetail(challengeId).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateUI(response.body());
                } else {
                    Toast.makeText(ChallengeDetailActivity.this, "상세 정보 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ChallengeDetailActivity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // UI에 상세 정보 반영하는 메서드
    private void updateUI(JsonObject challenge) {
        Glide.with(this)
                .load(challenge.get("image_url").getAsString())
                .into(challengeImage);

        titleView.setText(challenge.get("title").getAsString());
        descriptionView.setText(challenge.get("description").getAsString());
        pointsView.setText(challenge.get("points_reward").getAsInt() + " P");

        int max = challenge.get("max_participants").getAsInt();
        int current = challenge.get("participants").getAsInt();

        // maxParticipantsView, currentParticipantsView에 각각 세팅
        if(maxParticipantsView != null) {
            maxParticipantsView.setText("마감 인원: " + max + "명");
        }
        if(currentParticipantsView != null) {
            currentParticipantsView.setText("선착순 " + (max - current) + "명 남음");
        }

        String startDateStr = challenge.get("start_date").getAsString();
        String endDateStr = challenge.get("end_date").getAsString();

        // 챌린지 일정 표시 (예: 25.07.06 ~ 25.07.20)
        String formattedDateRange = formatDateRange(startDateStr, endDateStr);
        if (challengeDateRangeView != null) {
            challengeDateRangeView.setText(formattedDateRange);
        }

        // 모집 마감일까지 남은 시간 표시
        if (deadlineView != null) {
            String deadlineText = calculateDeadlineText(endDateStr);
            deadlineView.setText(deadlineText);
        }
    }

    // 날짜 범위 포맷팅 메서드
    private String formatDateRange(String start, String end) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");  // 서버 날짜 형식
        SimpleDateFormat displayFormat = new SimpleDateFormat("yy.MM.dd");    // 앱 표시 형식

        try {
            Date startDate = serverFormat.parse(start);
            Date endDate = serverFormat.parse(end);
            return displayFormat.format(startDate) + " ~ " + displayFormat.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return start + " ~ " + end;  // 포맷팅 실패 시 원본 그대로 표시
        }
    }

    // 모집 마감일까지 남은 시간 계산 메서드
    private String calculateDeadlineText(String endDateStr) {
        SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date now = new Date();
            Date endDate = serverFormat.parse(endDateStr);

            long diffMillis = endDate.getTime() - now.getTime();
            if (diffMillis <= 0) {
                return "모집이 마감되었습니다.";
            }

            long days = TimeUnit.MILLISECONDS.toDays(diffMillis);
            long hours = TimeUnit.MILLISECONDS.toHours(diffMillis) - (days * 24);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis) - (TimeUnit.MILLISECONDS.toHours(diffMillis) * 60);

            return String.format("모집 마감일 까지 : %d일 %02d시간 %02d분 전", days, hours, minutes);

        } catch (ParseException e) {
            e.printStackTrace();
            return "모집 마감일 정보 없음";
        }
    }
}