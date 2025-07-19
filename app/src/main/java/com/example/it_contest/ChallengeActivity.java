package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChallengeActivity extends BaseActivity {

    private TextView textMy, textAll, pointText, noChallengeText;
    private LinearLayout pointContainer;
    private GridLayout cardGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_main);

        setupBottomNavigation("challenge");

        textMy = findViewById(R.id.text_my);
        textAll = findViewById(R.id.text_all);
        pointText = findViewById(R.id.point);
        pointContainer = findViewById(R.id.point_container);
        noChallengeText = findViewById(R.id.no_challenge_text);
        cardGrid = findViewById(R.id.card_grid);

        // 포인트 클릭 -> 포인트 페이지로
        pointContainer.setOnClickListener(v -> {
            startActivity(new Intent(this, PointMainActivity.class));
        });

        // MY / 전체보기 토글
        textMy.setOnClickListener(v -> {
            textMy.setTextColor(getResources().getColor(android.R.color.black));
            textAll.setTextColor(getResources().getColor(android.R.color.darker_gray));
            loadChallenges(true);
        });

        textAll.setOnClickListener(v -> {
            textAll.setTextColor(getResources().getColor(android.R.color.black));
            textMy.setTextColor(getResources().getColor(android.R.color.darker_gray));
            loadChallenges(false);
        });

        // 첫 로드 (전체보기)
        loadChallenges(false);
    }

    private void loadChallenges(boolean onlyMyChallenges) {
        cardGrid.removeAllViews();
        noChallengeText.setVisibility(View.GONE);

        // 실제 서버 데이터 대신 테스트용 가짜 데이터 (2개만 표시)
        int challengeCount = 2;  // 서버 연동 시 변경
        if (challengeCount == 0) {
            noChallengeText.setVisibility(View.VISIBLE);
            return;
        }

        for (int i = 0; i < challengeCount; i++) {
            View card = getLayoutInflater().inflate(R.layout.challenge_card_item, cardGrid, false);

            // GridLayout 파라미터 (2열 맞추기)
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            card.setLayoutParams(params);

            // 카드 클릭 -> 챌린지 상세 페이지
            card.setOnClickListener(v -> {
                startActivity(new Intent(ChallengeActivity.this, ChallengeDetailActivity.class));
            });

            cardGrid.addView(card);
        }
    }
}
