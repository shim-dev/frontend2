package com.example.it_contest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class recipe_search_screen_activity extends BaseActivity {

    private LinearLayout layoutKeywords;
    private EditText etSearch;
    private ImageView btnSearch, btnClear;

    private RecyclerView rvRecent;
    private RecentSearchAdapter recentAdapter;
    private List<String> recentList = new ArrayList<>();
    private ApiService apiService;

    // ✅ FAB 관련
    private ImageButton btnFabMain, btnFabWrite, btnFabLocation;
    private LinearLayout layoutFabWrite, layoutFabLocation;
    private boolean isFabOpen = false;

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentSearchFromDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_search_screen);
        setupBottomNavigation("community");

        layoutKeywords = findViewById(R.id.layout_keywords);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        rvRecent = findViewById(R.id.rv_recent_search);
        btnClear = findViewById(R.id.btn_clear);

        apiService = RetrofitClient.getApiService();

        // ✅ 최근 검색 RecyclerView 세팅
        setupRecentSearchRecycler();

        loadRecentSearchFromDB();
        loadKeywordsFromDB();

        btnClear.setOnClickListener(v -> etSearch.setText(""));

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

        // ✅ FAB 연결
        setupFabButtons();
    }

    // ✅ FAB 초기화 및 클릭 이벤트
    private void setupFabButtons() {
        btnFabMain = findViewById(R.id.btn_fab_main);
        btnFabWrite = findViewById(R.id.btn_fab_write);
        btnFabLocation = findViewById(R.id.btn_fab_location);
        layoutFabWrite = findViewById(R.id.layout_fab_write);
        layoutFabLocation = findViewById(R.id.layout_fab_location);

        btnFabMain.setOnClickListener(v -> {
            if (isFabOpen) {
                layoutFabWrite.setVisibility(LinearLayout.GONE);
                layoutFabLocation.setVisibility(LinearLayout.GONE);
                btnFabMain.setImageResource(R.drawable.ic_float_logo);
            } else {
                layoutFabWrite.setVisibility(LinearLayout.VISIBLE);
                layoutFabLocation.setVisibility(LinearLayout.VISIBLE);
                btnFabMain.setImageResource(R.drawable.ic_float_logo_close);
            }
            isFabOpen = !isFabOpen;
        });

        btnFabWrite.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
        });

        btnFabLocation.setOnClickListener(v -> {
            Toast.makeText(this, "주변 저속노화 맛집 찾기!", Toast.LENGTH_SHORT).show();
            // 이동하려고 하는 페이지 (채림 언니 연결)
            //Todo
            // startActivity(new Intent(this, LocationSearchActivity.class));
        });
    }

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

    private void loadRecentSearchFromDB() {
        apiService.getSearchHistory().enqueue(new Callback<List<SearchHistory>>() {
            @Override
            public void onResponse(Call<List<SearchHistory>> call, Response<List<SearchHistory>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recentList.clear();
                    Set<String> seen = new HashSet<>();
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

    private void saveSearchToDB(String keyword) {
        Map<String, String> body = new HashMap<>();
        body.put("keyword", keyword);

        apiService.addSearchHistory(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadRecentSearchFromDB();
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

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
