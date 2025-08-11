package com.example.it_contest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RecipeListActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageButton clearSearchBtn, searchBtn;
    private Button sortLatestBtn, sortPopularBtn;
    private RecyclerView recipeRecyclerView;

    // 어댑터(카드형)
    private RecipeCardAdapter adapter;

    // 현재까지 로드된 원본 데이터(페이지 누적)
    private final List<Recipe> original = new ArrayList<>();
    // 화면에 표시할 리스트(필터/정렬 적용 결과)
    private final List<Recipe> showing = new ArrayList<>();

    // 정렬 상태
    private boolean sortLatest = true;

    // 페이징 상태
    private boolean isLoading = false;
    private boolean hasMore = true;
    private int page = 1;
    private static final int PAGE_SIZE = 20;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        searchEditText   = findViewById(R.id.searchEditText);
        clearSearchBtn   = findViewById(R.id.clearSearchBtn);
        searchBtn        = findViewById(R.id.searchBtn);
        sortLatestBtn    = findViewById(R.id.sortLatestBtn);
        sortPopularBtn   = findViewById(R.id.sortPopularBtn);
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);

        // 레이아웃 매니저/어댑터
        LinearLayoutManager lm = new LinearLayoutManager(this);
        recipeRecyclerView.setLayoutManager(lm);
        adapter = new RecipeCardAdapter(showing);
        recipeRecyclerView.setAdapter(adapter);
        recipeRecyclerView.setHasFixedSize(true);

        // 무한 스크롤
        recipeRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                super.onScrolled(rv, dx, dy);
                if (dy <= 0) return;
                int last = lm.findLastVisibleItemPosition();
                int total = adapter.getItemCount();
                if (!isLoading && hasMore && last >= total - 4) {
                    fetchNextPage();
                }
            }
        });

        // 검색
        searchBtn.setOnClickListener(v -> performSearch());
        searchEditText.setOnEditorActionListener((v, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
        clearSearchBtn.setOnClickListener(v -> {
            searchEditText.setText("");
            performSearch();
        });

        // 정렬
        updateSortUI(true);
        sortLatestBtn.setOnClickListener(v -> {
            if (!sortLatest) {
                sortLatest = true;
                updateSortUI(true);
                refreshAndLoad();
            }
        });
        sortPopularBtn.setOnClickListener(v -> {
            if (sortLatest) {
                sortLatest = false;
                updateSortUI(false);
                refreshAndLoad();
            }
        });

        // 최초 로드
        refreshAndLoad();
    }

    private void performSearch() {
        // 검색/정렬 조건이 바뀌면 1페이지부터 다시 로드
        refreshAndLoad();
    }

    /** 첫 페이지부터 다시 로드 */
    private void refreshAndLoad() {
        page = 1;
        hasMore = true;
        isLoading = false;

        original.clear();
        showing.clear();
        adapter.notifyDataSetChanged();

        fetchNextPage();
    }

    /** 다음 페이지 로드 (실제 API로 교체 지점) */
    private void fetchNextPage() {
        if (isLoading || !hasMore) return;
        isLoading = true;

        final String query = safeLower(searchEditText.getText() == null ? "" : searchEditText.getText().toString());
        final String sort  = sortLatest ? "latest" : "popular";

        // TODO: Retrofit/OkHttp 등 실제 API 호출로 교체
        // ex) api.getRecipes(page, PAGE_SIZE, sort, query).enqueue(new Callback<...>() { ... })
        recipeRecyclerView.postDelayed(() -> {
            List<Recipe> pageData = mockFetchPage(page, PAGE_SIZE, sort, query);

            if (pageData.isEmpty()) {
                hasMore = false;
            } else {
                original.addAll(pageData);
                page++;
                if (pageData.size() < PAGE_SIZE) hasMore = false;
            }

            applySortAndFilter();
            isLoading = false;
        }, 500);
    }

    /** 원본(original)에 대해 검색/정렬 적용 후 showing 반영 */
    private void applySortAndFilter() {
        String q = safeLower(searchEditText.getText() == null ? "" : searchEditText.getText().toString());

        // 필터
        showing.clear();
        if (TextUtils.isEmpty(q)) {
            showing.addAll(original);
        } else {
            for (Recipe r : original) {
                String t = safeLower(r.title);
                String g = safeLower(r.tags);
                if (t.contains(q) || g.contains(q)) {
                    showing.add(r);
                }
            }
        }

        // 정렬
        if (sortLatest) {
            Collections.sort(showing, (a, b) -> Long.compare(b.createdAt, a.createdAt));
        } else {
            Collections.sort(showing, (a, b) -> Integer.compare(b.views, a.views));
        }

        adapter.notifyDataSetChanged();
    }

    private void updateSortUI(boolean latest) {
        sortLatestBtn.setTypeface(null, latest ? Typeface.BOLD : Typeface.NORMAL);
        sortPopularBtn.setTypeface(null, latest ? Typeface.NORMAL : Typeface.BOLD);
        sortLatestBtn.setTextColor(Color.parseColor(latest ? "#000000" : "#999999"));
        sortPopularBtn.setTextColor(Color.parseColor(latest ? "#999999" : "#000000"));
    }

    private String safeLower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT);
    }

    // ====== 데모용 페이지 생성기 (API 연동 전 임시) ======
    private List<Recipe> mockFetchPage(int page, int size, String sort, String query) {
        List<Recipe> out = new ArrayList<>();
        long now = System.currentTimeMillis();
        int start = (page - 1) * size;

        for (int i = 0; i < size; i++) {
            int idx = start + i + 1;

            String titleBase = "두부면 레시피 " + idx;
            String title = TextUtils.isEmpty(query) ? titleBase : ("[" + query + "] " + titleBase);
            String tags = sort.equals("popular") ? "#조회많음 #핫" : "#최신 #업로드";
            int views = sort.equals("popular") ? (800 - idx * 3) : (idx * 7 % 500);

            out.add(new Recipe(
                    title,
                    tags,
                    "데모 설명 문구 " + idx,
                    "⏱ 10분    🔥 난이도 하    👤 1인분",
                    now - (idx * 1_000L),
                    Math.max(views, 0),
                    R.drawable.padthai // 임시 이미지
            ));
        }

        // 데모: 5페이지까지만
        if (page > 5) out.clear();
        return out;
    }
}

