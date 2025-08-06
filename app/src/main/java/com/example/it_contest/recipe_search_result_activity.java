package com.example.it_contest;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_search_result); // XML 파일명

        // 첫 화면에서 전달받은 검색어
        keyword = getIntent().getStringExtra("keyword");

        // 뷰 연결
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        btnBack = findViewById(R.id.btn_back);
        btnClear = findViewById(R.id.btn_clear);
        rvRecipes = findViewById(R.id.rv_recipes);

        // 검색어 입력창에 세팅
        etSearch.setText(keyword);

        // RecyclerView 설정
        rvRecipes.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RecipeAdapter(recipeList);
        rvRecipes.setAdapter(adapter);

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 검색 버튼 클릭
        btnSearch.setOnClickListener(v -> {
            String newKeyword = etSearch.getText().toString().trim();
            if (!newKeyword.isEmpty()) {
                searchFromDB(newKeyword);
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 검색창 X 버튼
        btnClear.setOnClickListener(v -> etSearch.setText(""));

        // 처음 화면 진입 시 바로 검색
        if (keyword != null && !keyword.isEmpty()) {
            searchFromDB(keyword);
        }
    }

    private void searchFromDB(String keyword) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.searchRecipes(keyword).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "응답 데이터: " + new Gson().toJson(response.body())); // ✅ 응답 찍기
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
