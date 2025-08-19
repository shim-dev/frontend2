package com.example.it_contest;

// 필요한 import
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.DietRecord;
import com.example.it_contest.network.FoodSearchResult;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 메인 페이지
 * - 링/배지: 각 끼니의 "최신 점수" 반영, 없으면 '—'
 * - 가운데 Total: 존재하는 끼니 평균
 */
public class MainPageActivity extends AppCompatActivity {

    private ScoreCircleView scoreCircleView;
    private TextView greetingText;
    private TextView chatDate1, chatDate2, chatDate3; // 최근 기록 3개 날짜 칩

    // 식사 카드 배지(TextView는 원형 까만 배지 배경)
    private TextView badgeMorning, badgeLunch, badgeDinner, badgeSnack;

    private String selectedDate;      // yyyy-MM-dd
    private String nickname = "손님";
    private String userEmail = "bb@naver.com"; // 테스트 계정

    // (예시) 아침 프리필 상태 보관 — 필요 시 점심/저녁/간식도 동일 패턴으로 확장
    private String           breakfastRecordId = null;
    private FoodSearchResult breakfastFood     = null;

    /** 끼니명 표준화: 영/한/변형 → "아침/점심/저녁/간식" */
    private String normMealType(String t) {
        if (t == null) return "";
        String v = t.trim().toLowerCase(Locale.ROOT);
        switch (v) {
            case "아침":
            case "morning":
            case "breakfast":
                return "아침";
            case "점심":
            case "lunch":
                return "점심";
            case "저녁":
            case "석식":
            case "dinner":
                return "저녁";
            case "간식":
            case "스낵":
            case "snack":
                return "간식";
            default:
                return t; // 합산표에 없으면 건너뜀
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.main_page);

        // ---------------- 테스트용 로그인 값 주입 ----------------
        final String testEmail = "bb@naver.com";
        final String testNickname = "shim";
        userEmail = testEmail;

        SharedPreferences pref = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_token", "dummy-jwt-token-for-testing");
        editor.putString("user_email", testEmail);
        editor.apply();
        // -------------------------------------------------------

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ---------- UI 바인딩 ----------
        greetingText      = findViewById(R.id.greetingText);
        scoreCircleView   = findViewById(R.id.scoreCircleView);
        chatDate1         = findViewById(R.id.chatDate1);
        chatDate2         = findViewById(R.id.chatDate2);
        chatDate3         = findViewById(R.id.chatDate3);

        // 식사 카드 배지 (XML에서 이 id로 만들어져 있어야 함)
        badgeMorning = findViewById(R.id.badgeMorning);
        badgeLunch   = findViewById(R.id.badgeLunch);
        badgeDinner  = findViewById(R.id.badgeDinner);
        badgeSnack   = findViewById(R.id.badgeSnack);

        updateUI(testNickname); // 첫 렌더 문구

        // ---------- 보텀시트 결과 수신(저장/삭제) → 점수/칩/배지 즉시 갱신 ----------
        String[] SAVED_KEYS = {
                BreakfastBottomSheet.RESULT_KEY_SAVED,
                LunchBottomSheet.RESULT_KEY_SAVED,
                DinnerBottomSheet.RESULT_KEY_SAVED,
                SnackBottomSheet.RESULT_KEY_SAVED
        };
        String[] DELETED_KEYS = {
                BreakfastBottomSheet.RESULT_KEY_DELETED,
                LunchBottomSheet.RESULT_KEY_DELETED,
                DinnerBottomSheet.RESULT_KEY_DELETED,
                SnackBottomSheet.RESULT_KEY_DELETED
        };
        for (String k : SAVED_KEYS) {
            getSupportFragmentManager().setFragmentResultListener(k, this, (key, bundle) -> {
                breakfastRecordId = bundle.getString("record_id", null);
                breakfastFood     = bundle.getParcelable("food");
                refreshScoresForDate(userEmail, selectedDate);
                loadChatHistory(userEmail);
            });
        }
        for (String k : DELETED_KEYS) {
            getSupportFragmentManager().setFragmentResultListener(k, this, (key, bundle) -> {
                refreshScoresForDate(userEmail, selectedDate);
                loadChatHistory(userEmail);
            });
        }

        // ---------- 사용자 정보 ----------
        ApiService api = RetrofitClient.getApiService();
        api.getMe(userEmail).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null && response.body().has("nickname")) {
                    nickname = response.body().get("nickname").getAsString();
                    updateUI(nickname);

                    // 기본 선택 날짜: 오늘
                    selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // 초기 로딩
                    refreshScoresForDate(userEmail, selectedDate);
                    loadChatHistory(userEmail); // 3칩 텍스트/태그 갱신
                    attachHistoryChipClicks();  // 클릭 리스너 등록
                } else {
                    Toast.makeText(MainPageActivity.this, "/me 요청 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                greetingText.setText("건강한 하루 시작해볼까요?\n오프라인");
                t.printStackTrace();
            }
        });

        // ---------- 식사 버튼 ----------
        findViewById(R.id.morningBtn).setOnClickListener(v -> openMealSheet("아침"));
        findViewById(R.id.lunchBtn).setOnClickListener(v -> openMealSheet("점심"));
        findViewById(R.id.dinnerBtn).setOnClickListener(v -> openMealSheet("저녁"));
        findViewById(R.id.snackBtn).setOnClickListener(v -> openMealSheet("간식"));
    }

    /** 채팅기록 3칩 클릭 → 칩에 저장된 yyyy-MM-dd로 선택 변경 & 점수 갱신 */
    private void attachHistoryChipClicks() {
        TextView[] chips = {chatDate1, chatDate2, chatDate3};
        for (TextView chip : chips) {
            chip.setOnClickListener(v -> {
                Object tag = v.getTag(); // updateHistoryUI에서 yyyy-MM-dd 저장함
                if (tag instanceof String) {
                    selectedDate = (String) tag;
                    refreshScoresForDate(userEmail, selectedDate);
                }
            });
        }
    }

    /** 선택된 날짜의 "각 끼니 최신 점수"를 반영 */
    private void refreshScoresForDate(String email, String dateYmd) {
        ApiService api = RetrofitClient.getApiService();
        api.getHistory(email).enqueue(new Callback<List<DietRecord>>() {
            @Override
            public void onResponse(Call<List<DietRecord>> call, Response<List<DietRecord>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MainPageActivity.this, "점수 갱신 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                // 끼니별 최신 레코드(같은 날 안에서 timestamp 최대)를 보관
                Map<String, DietRecord> latest = new HashMap<>();
                String[] types = {"아침","점심","저녁","간식"};

                for (DietRecord r : response.body()) {
                    if (!isSameDay(r.getTimestamp(), dateYmd)) continue;

                    String key = normMealType(r.getMealType());
                    boolean known = false;
                    for (String t : types) if (t.equals(key)) { known = true; break; }
                    if (!known) continue;

                    // ISO8601은 문자열 비교로 최신 판단 가능(동일 타임존 가정)
                    DietRecord old = latest.get(key);
                    if (old == null || safeStr(r.getTimestamp()).compareTo(safeStr(old.getTimestamp())) > 0) {
                        latest.put(key, r);
                    }
                }

                // 최신 점수 꺼내기 (없으면 0)
                Integer morning = latest.containsKey("아침") ? (int)Math.round(extractScore(latest.get("아침"))) : null;
                Integer lunch   = latest.containsKey("점심") ? (int)Math.round(extractScore(latest.get("점심"))) : null;
                Integer dinner  = latest.containsKey("저녁") ? (int)Math.round(extractScore(latest.get("저녁"))) : null;
                Integer snack   = latest.containsKey("간식") ? (int)Math.round(extractScore(latest.get("간식"))) : null;

                // 링에 반영(없으면 0으로)
                scoreCircleView.setScores(
                        morning != null ? morning : 0,
                        lunch   != null ? lunch   : 0,
                        dinner  != null ? dinner  : 0,
                        snack   != null ? snack   : 0
                );

                // Total: 존재하는 끼니 평균
                int present = 0; double totalSum = 0;
                if (morning != null) { totalSum += morning; present++; }
                if (lunch   != null) { totalSum += lunch;   present++; }
                if (dinner  != null) { totalSum += dinner;  present++; }
                if (snack   != null) { totalSum += snack;   present++; }
                Integer totalOverride = (present > 0) ? (int)Math.round(totalSum / present) : null;
                scoreCircleView.setTotalOverride(totalOverride);
                scoreCircleView.invalidate();

                // 카드 배지 갱신 (없으면 '—')
                updateMealBadges(morning, lunch, dinner, snack);
            }

            @Override
            public void onFailure(Call<List<DietRecord>> call, Throwable t) {
                Toast.makeText(MainPageActivity.this, "점수 갱신 실패(네트워크)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safeStr(String s) { return s == null ? "" : s; }

    // 배지 갱신 유틸
    private void updateMealBadges(Integer morning, Integer lunch, Integer dinner, Integer snack) {
        setBadge(badgeMorning, morning);
        setBadge(badgeLunch,   lunch);
        setBadge(badgeDinner,  dinner);
        setBadge(badgeSnack,   snack);
    }

    private void setBadge(TextView tv, Integer scoreOrNull) {
        if (tv == null) return;
        if (scoreOrNull == null) {
            tv.setText("—");
            tv.setAlpha(0.5f); // 데이터 없을 때 살짝 흐리게
        } else {
            int clamped = Math.max(0, Math.min(scoreOrNull, 100));
            tv.setText(String.valueOf(clamped));
            tv.setAlpha(1f);
        }
    }

    // ---------- 히스토리 리스트(최근 날짜 3개) ----------
    private void loadChatHistory(String email) {
        ApiService api = RetrofitClient.getApiService();
        api.getHistory(email).enqueue(new Callback<List<DietRecord>>() {
            @Override
            public void onResponse(Call<List<DietRecord>> call, Response<List<DietRecord>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateHistoryUI(response.body());
                } else {
                    Toast.makeText(MainPageActivity.this, "/history 요청 실패: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<DietRecord>> call, Throwable t) {
                Toast.makeText(MainPageActivity.this, "채팅 기록을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void updateHistoryUI(List<DietRecord> records) {
        TextView[] chips = {chatDate1, chatDate2, chatDate3};

        // 일단 숨김
        for (TextView chip : chips) chip.setVisibility(TextView.GONE);

        // 최신 3개만 표시 (응답이 최신순이라고 가정)
        for (int i = 0; i < records.size() && i < chips.length; i++) {
            DietRecord record = records.get(i);
            TextView chip = chips[i];

            // 칩 텍스트: "MM / dd"
            chip.setText(formatDateDisplay(record.getTimestamp()));
            chip.setVisibility(TextView.VISIBLE);

            // 클릭 시 사용할 yyyy-MM-dd 를 태그로 저장
            chip.setTag(ymdFromTimestamp(record.getTimestamp()));
        }
    }

    // ---------- 유틸 ----------
    private double extractScore(DietRecord r) {
        if (r == null) return 0.0;
        try {
            return r.getMealScore();
        } catch (Throwable ignore) {
            try {
                return (r.getMind() != null) ? r.getMind().getMealScore() : 0.0;
            } catch (Throwable e) {
                return 0.0;
            }
        }
    }

    /** ISO 8601 문자열이 같은 yyyy-MM-dd 인지 비교 */
    private boolean isSameDay(String iso, String ymd) {
        if (iso == null || ymd == null) return false;
        // 서버 ISO 문자열이 'YYYY-MM-DD...' 형태이므로 앞 10자리만 직접 비교
        return iso.length() >= 10 && ymd.equals(iso.substring(0, 10));
    }

    /** "MM / dd" 표기 */
    private String formatDateDisplay(String timestamp) {
        if (timestamp == null) return "??/??";
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
            SimpleDateFormat formatter = new SimpleDateFormat("MM / dd", Locale.getDefault());
            Date date = parser.parse(timestamp);
            return formatter.format(date);
        } catch (Exception e) {
            if (timestamp.length() >= 10) return timestamp.substring(5, 10).replace("-", " / ");
            return "??/??";
        }
    }

    /** yyyy-MM-dd 추출(칩 tag용) */
    private String ymdFromTimestamp(String timestamp) {
        if (timestamp == null) return null;
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.ENGLISH);
            SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date d = parser.parse(timestamp);
            return ymd.format(d);
        } catch (Exception e) {
            return (timestamp.length() >= 10) ? timestamp.substring(0, 10) : null;
        }
    }

    /** 상단 인사말 업데이트 */
    private void updateUI(String newNickname) {
        this.nickname = newNickname;
        if (greetingText == null) greetingText = findViewById(R.id.greetingText);
        if ("손님".equals(nickname)) greetingText.setText("건강한 하루 시작해볼까요?\n" + nickname);
        else greetingText.setText("건강한 하루 시작해볼까요?\n" + nickname + "님");
    }

    /** 보텀시트 열기 */
    private void openMealSheet(String mealType) {
        Bundle args = new Bundle();
        args.putString("date", selectedDate);
        args.putString("nickname", nickname);
        args.putString("meal_type", mealType);

        switch (mealType) {
            case "아침": {
                BreakfastBottomSheet sheet = BreakfastBottomSheet.newInstance(
                        selectedDate, nickname, "아침");
                sheet.show(getSupportFragmentManager(), "BreakfastBottomSheet");
                break;
            }
            case "점심": {
                LunchBottomSheet s = new LunchBottomSheet();
                s.setArguments(args);
                s.show(getSupportFragmentManager(), "LunchBottomSheet");
                break;
            }
            case "저녁": {
                DinnerBottomSheet s = new DinnerBottomSheet();
                s.setArguments(args);
                s.show(getSupportFragmentManager(), "DinnerBottomSheet");
                break;
            }
            case "간식": {
                SnackBottomSheet s = new SnackBottomSheet();
                s.setArguments(args);
                s.show(getSupportFragmentManager(), "SnackBottomSheet");
                break;
            }
        }
    }
}
