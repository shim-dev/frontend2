package com.example.it_contest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected LinearLayout menuHome, menuCommunity, menuChallenge, menuMy;
    protected ImageView iconHome, iconCommunity, iconChallenge, iconMy;
    protected TextView textHome, textCommunity, textChallenge, textMy;

    protected int black, gray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // BaseActivity에서는 layout을 설정하지 않음 — 자식 Activity에서 setContentView() 호출해야 함
    }

    protected void setupBottomNavigation(String selected) {
        // 색상 설정
        black = Color.parseColor("#000000");
        gray = Color.parseColor("#999999");

        // 메뉴 아이템 연결
        menuHome = findViewById(R.id.menu_home);
        menuCommunity = findViewById(R.id.menu_community);   // 기존 map → community로 변경
        menuChallenge = findViewById(R.id.menu_challenge);
        menuMy = findViewById(R.id.menu_my);

        iconHome = findViewById(R.id.icon_home);
        iconCommunity = findViewById(R.id.icon_community);   // map 아이콘 → community 아이콘으로 변경
        iconChallenge = findViewById(R.id.icon_challenge);
        iconMy = findViewById(R.id.icon_my);

        textHome = findViewById(R.id.text_home);
        textCommunity = findViewById(R.id.text_community);
        textChallenge = findViewById(R.id.text_challenge);
        textMy = findViewById(R.id.text_my);

        // 선택된 메뉴 반영
        selectMenu(selected);

        // 각 메뉴 클릭 시 이동
        menuHome.setOnClickListener(v -> {
            if (!selected.equals("home")) {
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        menuCommunity.setOnClickListener(v -> {
            if (!selected.equals("community")) {
                // TODO: CommunityActivity로 이동
                // startActivity(new Intent(this, CommunityActivity.class));
            }
        });

        menuChallenge.setOnClickListener(v -> {
            // challenge는 항상 ChallengeActivity로 이동하도록 (selected 체크 안 함)
            startActivity(new Intent(this, ChallengeActivity.class));
            overridePendingTransition(0, 0);
        });

        menuMy.setOnClickListener(v -> {
            if (!selected.equals("my")) {
                // TODO: 마이페이지 Activity로 이동
                // startActivity(new Intent(this, MyPageActivity.class));
            }
        });
    }

    private void selectMenu(String selected) {
        iconHome.setColorFilter(selected.equals("home") ? black : gray);
        iconCommunity.setColorFilter(selected.equals("community") ? black : gray);
        iconChallenge.setColorFilter(selected.equals("challenge") ? black : gray);
        iconMy.setColorFilter(selected.equals("my") ? black : gray);

        textHome.setVisibility(selected.equals("home") ? View.VISIBLE : View.GONE);
        textCommunity.setVisibility(selected.equals("community") ? View.VISIBLE : View.GONE);
        textChallenge.setVisibility(selected.equals("challenge") ? View.VISIBLE : View.GONE);
        textMy.setVisibility(selected.equals("my") ? View.VISIBLE : View.GONE);
    }
}
