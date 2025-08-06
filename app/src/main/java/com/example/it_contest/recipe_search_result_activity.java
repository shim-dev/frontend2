package com.example.it_contest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.it_contest.model.Recipe;
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class recipe_search_result_activity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView btnSearch, btnBack, btnClear;
    private RecyclerView rvRecipes;
    private RecipeAdapter adapter;
    private List<Recipe> recipeList = new ArrayList<>();
    private String keyword;
    private TextView tvLatest, tvViews; // ✅ 추가

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_search_result);

        keyword = getIntent().getStringExtra("keyword");

        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        btnBack = findViewById(R.id.btn_back);
        btnClear = findViewById(R.id.btn_clear);
        rvRecipes = findViewById(R.id.rv_recipes);
        tvLatest = findViewById(R.id.tv_latest); // ✅ 추가
        tvViews = findViewById(R.id.tv_views);   // ✅ 추가

        etSearch.setText(keyword);

        rvRecipes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipeList);
        rvRecipes.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> {
            String newKeyword = etSearch.getText().toString().trim();
            if (!newKeyword.isEmpty()) {
                searchFromDB(newKeyword, "latest"); // 기본 최신순
                setSortSelection(true);
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        btnClear.setOnClickListener(v -> etSearch.setText(""));

        // 최신순 클릭
        tvLatest.setOnClickListener(v -> {
            setSortSelection(true);
            searchFromDB(keyword, "latest");
        });

        // 조회순 클릭
        tvViews.setOnClickListener(v -> {
            setSortSelection(false);
            searchFromDB(keyword, "views");
        });

        // 처음 화면 진입 시 최신순 검색
        if (keyword != null && !keyword.isEmpty()) {
            setSortSelection(true);
            searchFromDB(keyword, "latest");
        }
    }

    // ✅ 정렬 버튼 색상/굵기 변경
    private void setSortSelection(boolean isLatest) {
        if (isLatest) {
            tvLatest.setTextColor(Color.BLACK);
            tvLatest.setTypeface(null, Typeface.BOLD);
            tvViews.setTextColor(Color.parseColor("#999999"));
            tvViews.setTypeface(null, Typeface.NORMAL);
        } else {
            tvViews.setTextColor(Color.BLACK);
            tvViews.setTypeface(null, Typeface.BOLD);
            tvLatest.setTextColor(Color.parseColor("#999999"));
            tvLatest.setTypeface(null, Typeface.NORMAL);
        }
    }

    // ✅ 정렬 옵션 추가된 검색 메서드
    private void searchFromDB(String keyword, String sort) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.searchRecipes(keyword, sort).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "응답 데이터: " + new Gson().toJson(response.body()));
                    recipeList.clear();
                    recipeList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(recipe_search_result_activity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(recipe_search_result_activity.this, "서버 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
