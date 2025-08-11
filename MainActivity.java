package com.example.it_contest;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ScoreCircleView scoreCircleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.main_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ ScoreCircleView 데이터 세팅
        scoreCircleView = findViewById(R.id.scoreCircleView);
        scoreCircleView.setScores(85, 55, 63, 30);

        // ✅ 아침 버튼 → BreakfastBottomSheet
        findViewById(R.id.morningBtn).setOnClickListener(v -> {
            BreakfastBottomSheet bottomSheet = new BreakfastBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "BreakfastBottomSheet");
        });

        // ✅ 점심 버튼 → LunchBottomSheet
        findViewById(R.id.lunchBtn).setOnClickListener(v -> {
            LunchBottomSheet bottomSheet = new LunchBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "LunchBottomSheet");
        });

        // ✅ 저녁 버튼 → DinnerBottomSheet
        findViewById(R.id.dinnerBtn).setOnClickListener(v -> {
            DinnerBottomSheet bottomSheet = new DinnerBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "DinnerBottomSheet");
        });

        // ✅ 간식 버튼 → SnackBottomSheet
        findViewById(R.id.snackBtn).setOnClickListener(v -> {
            SnackBottomSheet bottomSheet = new SnackBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "SnackBottomSheet");
        });
    }
}

