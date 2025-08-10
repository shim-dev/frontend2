package com.example.it_contest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointHistoryActivity extends BaseActivity
 {
    private List<PointHistoryItem> fullItemList;
    private RecyclerView recyclerView;
    private PointHistoryAdapter adapter;
     private TextView availablePointTextView, earnedPointTextView, usedPointTextView;



     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_history);

        ApiService apiService = RetrofitClient.getApiService();
         // SharedPreferences에서 nickname 가져오기
         SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
         String nickname = prefs.getString("nickname", null);

         if (nickname == null) {
             Log.e("HISTORY", "로그인 정보 없음");
             Intent intent = new Intent(PointHistoryActivity.this, LoginActivity.class);
             startActivity(intent);
             finish();
             return;
         }

         Call<JsonArray> call = apiService.getPointHistory(nickname);  //


         call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonArray historyList = response.body();

                    int totalEarned = 0;
                    int totalUsed = 0;

                    fullItemList.clear();  // 기존 더미 제거

                    for (JsonElement element : historyList) {
                        JsonObject item = element.getAsJsonObject();
                        String type = item.get("type").getAsString();
                        String desc = item.get("description").getAsString();
                        int point = item.get("points").getAsInt();
                        String date = item.get("date").getAsString();

                        if (type.equals("earn")) {
                            totalEarned += point;
                        } else if (type.equals("use")) {
                            totalUsed += point;
                        }

                        String pointString = (type.equals("earn") ? "+" : "-") + point + "P";
                        String typeDesc = (type.equals("earn") ? "적립(" : "사용(") + point + "P)";
                        String dateDetail = date + "  " + desc;

                        fullItemList.add(new PointHistoryItem(desc, pointString, dateDetail, typeDesc));
                    }

                    // 계산한 값으로 상단 텍스트뷰 갱신
                    int available = totalEarned - totalUsed;
                    availablePointTextView.setText("가용 포인트 : " + available + "P");
                    earnedPointTextView.setText("적립 포인트 : " + totalEarned + "P");
                    usedPointTextView.setText("사용 포인트 : " + totalUsed + "P");

                    adapter.updateData(fullItemList);

                    adapter.updateData(fullItemList); // 이걸로 UI 반영
                } else {
                    Log.e("HISTORY", "응답 실패: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("HISTORY", "서버 연결 실패", t);
            }
        });

         availablePointTextView = findViewById(R.id.available_point);
         earnedPointTextView = findViewById(R.id.earned_point);
         usedPointTextView = findViewById(R.id.used_point);


        // 하단바 세팅
        setupBottomNavigation("my");

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.point_history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fullItemList = new ArrayList<>();
        fullItemList.add(new PointHistoryItem("챌린지 참여", "-50P", "2025.07.01  하루 500보 걷기", "사용(700P)"));
        fullItemList.add(new PointHistoryItem("챌린지 성공금", "+50P", "2025.06.30  하루 500보 걷기", "적립(750P)"));
        fullItemList.add(new PointHistoryItem("포인트 충전", "+500P", "2025.06.28  500P 충전", "적립(700P)"));

        adapter = new PointHistoryAdapter(fullItemList);
        recyclerView.setAdapter(adapter);

        TextView tabAll = findViewById(R.id.tab_all);
        TextView tabEarn = findViewById(R.id.tab_earn);
        TextView tabUse = findViewById(R.id.tab_use);

        tabAll.setOnClickListener(v -> {
            adapter.updateData(fullItemList);
            setTabStyle(tabAll, tabEarn, tabUse);
        });

        tabEarn.setOnClickListener(v -> {
            List<PointHistoryItem> earnList = new ArrayList<>();
            for (PointHistoryItem item : fullItemList) {
                if (item.getPoint().trim().startsWith("+")) earnList.add(item);
            }
            adapter.updateData(earnList);
            setTabStyle(tabEarn, tabAll, tabUse);
        });

        tabUse.setOnClickListener(v -> {
            List<PointHistoryItem> useList = new ArrayList<>();
            for (PointHistoryItem item : fullItemList) {
                if (item.getPoint().trim().startsWith("-")) useList.add(item);
            }
            adapter.updateData(useList);
            setTabStyle(tabUse, tabAll, tabEarn);
        });
    }


    private void setTabStyle(TextView selected, TextView other1, TextView other2) {
        selected.setTextColor(getResources().getColor(android.R.color.black));
        selected.setTypeface(null, android.graphics.Typeface.BOLD);

        other1.setTextColor(android.graphics.Color.parseColor("#66000000"));
        other1.setTypeface(null, android.graphics.Typeface.NORMAL);

        other2.setTextColor(android.graphics.Color.parseColor("#66000000"));
        other2.setTypeface(null, android.graphics.Typeface.NORMAL);
    }
}
