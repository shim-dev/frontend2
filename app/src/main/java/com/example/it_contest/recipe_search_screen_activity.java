package com.example.it_contest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.it_contest.model.SearchHistory;
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class recipe_search_screen_activity extends AppCompatActivity {

    private LinearLayout layoutKeywords;
    private EditText etSearch;
    private ImageView btnSearch;

    // ✅ 최근 검색 관련
    private RecyclerView rvRecent;
    private RecentSearchAdapter recentAdapter;
    private List<String> recentList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentSearchFromDB(); // ✅ 화면 재진입 시 DB에서 다시 불러오기
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_search_screen);

        layoutKeywords = findViewById(R.id.layout_keywords);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        rvRecent = findViewById(R.id.rv_recent_search);

        apiService = RetrofitClient.getApiService();

        // ✅ RecyclerView 먼저 세팅
        setupRecentSearchRecycler();

        // ✅ 데이터 로드
        loadRecentSearchFromDB();
        loadKeywordsFromDB();

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                saveSearchToDB(keyword);
                Intent intent = new Intent(this, recipe_search_result_activity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ RecyclerView 세팅
    private void setupRecentSearchRecycler() {
        recentAdapter = new RecentSearchAdapter(recentList, position -> {
            String keyword = recentList.get(position);
            apiService.deleteSearchHistory(keyword).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    recentList.remove(position);
                    recentAdapter.notifyItemRemoved(position);
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        });

        rvRecent.setLayoutManager(new LinearLayoutManager(this));
        rvRecent.setAdapter(recentAdapter);

        // 전체 삭제 버튼 클릭
        findViewById(R.id.tv_clear_all).setOnClickListener(v -> {
            apiService.clearSearchHistory().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    recentList.clear();
                    recentAdapter.notifyDataSetChanged();
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {}
            });
        });
    }

    // ✅ DB에서 최근 검색 불러오기
    private void loadRecentSearchFromDB() {
        apiService.getSearchHistory().enqueue(new Callback<List<SearchHistory>>() {
            @Override
            public void onResponse(Call<List<SearchHistory>> call, Response<List<SearchHistory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recentList.clear();
                    Set<String> seen = new HashSet<>(); // ✅ 중복 체크용
                    for (SearchHistory item : response.body()) {
                        String kw = item.getKeyword();
                        if (kw != null && !kw.isEmpty() && !seen.contains(kw)) {
                            seen.add(kw);
                            recentList.add(kw);
                        }
                    }
                    recentAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFailure(Call<List<SearchHistory>> call, Throwable t) {}
        });
    }



    // ✅ 검색 시 DB에 저장
    private void saveSearchToDB(String keyword) {
        Map<String, String> body = new HashMap<>();
        body.put("keyword", keyword);

        apiService.addSearchHistory(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadRecentSearchFromDB(); // 저장 후 UI 갱신
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    // 기존 키워드 불러오기
    private void loadKeywordsFromDB() {
        apiService.getKeywords().enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> keywords = (List<String>) response.body().get("keywords");

                    if (keywords != null && !keywords.isEmpty()) {
                        for (String keyword : keywords) {
                            addKeywordTag(keyword);
                        }
                    }
                } else {
                    Toast.makeText(recipe_search_screen_activity.this, "서버 응답 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(recipe_search_screen_activity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 기존 키워드 태그 추가
    private void addKeywordTag(String keyword) {
        TextView tag = new TextView(this);
        tag.setText("# " + keyword);
        tag.setTextColor(Color.WHITE);
        tag.setTextSize(14);
        tag.setPadding(32, 16, 32, 16);
        tag.setBackgroundResource(R.drawable.keyword_background);
        tag.setGravity(android.view.Gravity.CENTER);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tag.setLayoutParams(params);

        layoutKeywords.addView(tag);
    }
}
