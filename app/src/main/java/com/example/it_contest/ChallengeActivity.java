package com.example.it_contest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;


import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChallengeActivity extends BaseActivity {

    private TextView textMy, textAll, pointText, noChallengeText;
    private LinearLayout pointContainer;
    private GridLayout cardGrid;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_main);

        setupBottomNavigation("challenge");


        tabLayout = findViewById(R.id.tab_layout);
        pointText = findViewById(R.id.point);
        pointContainer = findViewById(R.id.point_container);
        noChallengeText = findViewById(R.id.no_challenge_text);
        cardGrid = findViewById(R.id.card_grid);



        //  Tab 추가
        tabLayout.addTab(tabLayout.newTab().setText("MY").setContentDescription("내 챌린지"));
        tabLayout.addTab(tabLayout.newTab().setText("전체").setContentDescription("전체 챌린지"));

        //  Tab 선택 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean isMy = (tab.getPosition() == 0);
                loadChallenges(isMy);
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        //  첫 로딩은 "MY" 챌린지
        loadChallenges(true);
        //  로그인 정보에서 닉네임 추출 및 포인트 불러오기
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);

        if (nickname != null) {
            loadUserPoint(nickname);  //  포인트 표시용 함수 호출
        }



    }
    //  사용자 포인트 불러오기
    private void loadUserPoint(String nickname) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<JsonObject> call = apiService.getUserInfo(nickname); // ⚠getUserInfo() 메서드가 있어야 함

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int point = response.body().get("point").getAsInt();
                    pointText.setText(point + "P");
                } else {
                    pointText.setText("0P");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                pointText.setText("0P");
            }
        });
    }

    /** 서버에서 챌린지 불러오기 */
    private void loadChallenges(boolean onlyMyChallenges) {
        cardGrid.removeAllViews();
        noChallengeText.setVisibility(View.GONE);

        String filter = onlyMyChallenges ? "my" : "all";
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);

        if (nickname == null) {
            noChallengeText.setText("로그인 정보가 없습니다. 다시 로그인해주세요.");
            noChallengeText.setVisibility(View.VISIBLE);
            return;
        }


        ApiService apiService = RetrofitClient.getApiService();
        apiService.getChallenges(filter, nickname).enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API Response", response.body().toString());
                    JsonElement element = response.body();

                    if (element.isJsonArray()) {
                        JsonArray challenges = element.getAsJsonArray();

                        if (challenges.size() == 0) {
                            noChallengeText.setVisibility(View.VISIBLE);
                            return;
                        }

                        // 기존 챌린지 배열 처리 로직 계속
                        for (int i = 0; i < challenges.size(); i++) {
                            JsonObject challenge = challenges.get(i).getAsJsonObject();
                            View card = getLayoutInflater().inflate(R.layout.challenge_card_item, cardGrid, false);

                            bindChallengeData(card, challenge);

                            card.setOnClickListener(v -> {
                                Intent intent;
                                boolean joined = challenge.has("joined") && challenge.get("joined").getAsBoolean();

                                if (joined) {
                                    intent = new Intent(ChallengeActivity.this, ChallengeVerificationActivity.class);
                                } else {
                                    intent = new Intent(ChallengeActivity.this, ChallengeDetailActivity.class);
                                }

                                intent.putExtra("challenge_id", challenge.get("_id").getAsString());
                                startActivity(intent);
                            });



                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                            params.width = 0;
                            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                            params.setMargins(8, 8, 8, 8);
                            card.setLayoutParams(params);

                            cardGrid.addView(card);
                        }

                        if (challenges.size() % 2 == 1) {
                            View dummy = new View(ChallengeActivity.this);
                            GridLayout.LayoutParams dummyParams = new GridLayout.LayoutParams();
                            dummyParams.width = 0;
                            dummyParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
                            dummyParams.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                            dummy.setLayoutParams(dummyParams);
                            dummy.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                            cardGrid.addView(dummy);
                        }
                    } else if (element.isJsonObject()) {
                        // 예: { "message": "참여 중인 챌린지가 없습니다." }
                        noChallengeText.setText(element.getAsJsonObject().get("message").getAsString());
                        noChallengeText.setVisibility(View.VISIBLE);
                    } else {
                        noChallengeText.setVisibility(View.VISIBLE);
                    }
                } else {
                    noChallengeText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                noChallengeText.setText("서버 연결 실패: " + t.getMessage());
                noChallengeText.setVisibility(View.VISIBLE);
            }
        });

    }


    /** 카드 하나에 데이터 채우기 */
    private void bindChallengeData(View card, JsonObject challenge) {
        ImageView imageView = card.findViewById(R.id.challenge_image);
        TextView titleView = card.findViewById(R.id.challenge_title);
        TextView pointsView = card.findViewById(R.id.points_text);
        TextView spotsView = card.findViewById(R.id.spots_text);
        TextView startView = card.findViewById(R.id.start_text);
        TextView participantsBadge = card.findViewById(R.id.participants_text); // ★ 추가

        // 이미지
        String imageUrl = challenge.get("image_url").getAsString();
        Glide.with(this).load(imageUrl).centerCrop().into(imageView);

        // 제목/포인트
        titleView.setText(challenge.get("title").getAsString());
        pointsView.setText(challenge.get("points_reward").getAsInt() + " P");

        // 현재 인원 (백엔드에서 participants=정수로 내려옴)
        int current;
        if (challenge.has("participants") && challenge.get("participants").isJsonPrimitive()) {
            current = challenge.get("participants").getAsInt();
        } else if (challenge.has("joined_users") && challenge.get("joined_users").isJsonArray()) {
            // 혹시 participants 누락 시 백업 계산
            current = challenge.get("joined_users").getAsJsonArray().size();
        } else {
            current = 0;
        }
        participantsBadge.setText(String.valueOf(current)); // ★ 배지에 표시

        // 남은 인원
        int max = challenge.get("max_participants").getAsInt();
        int remain = Math.max(0, max - current);
        spotsView.setText("선착순 " + remain + "명 남음");

        // 시작 날짜
        startView.setText(challenge.get("start_date").getAsString() + " 시작");
    }

}