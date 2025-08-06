package com.example.it_contest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class recipe_search_screen_activity extends AppCompatActivity {

    private LinearLayout layoutKeywords;
    private EditText etSearch;
    private ImageView btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_search_screen);

        layoutKeywords = findViewById(R.id.layout_keywords);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);

        loadKeywordsFromDB();

        // 돋보기 버튼 클릭 → 다음 화면으로 이동
        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(recipe_search_screen_activity.this, recipe_search_result_activity.class);
                intent.putExtra("keyword", keyword);
                startActivity(intent);
            } else {
                Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadKeywordsFromDB() {
        ApiService apiService = RetrofitClient.getApiService();

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
