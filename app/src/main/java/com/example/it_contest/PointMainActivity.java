package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PointMainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_main);

        setupBottomNavigation("challenge");

        ImageButton backButton = findViewById(R.id.back_button);
        TextView pointText = findViewById(R.id.point);

        LinearLayout menuBox = findViewById(R.id.menu_box);
        LinearLayout historyLayout = (LinearLayout) menuBox.getChildAt(0);
        LinearLayout chargeLayout = (LinearLayout) menuBox.getChildAt(2);
        LinearLayout refundLayout = (LinearLayout) menuBox.getChildAt(4);

        pointText.setText("700 P");

        backButton.setOnClickListener(v -> onBackPressed());

        historyLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PointMainActivity.this, PointHistoryActivity.class);
            startActivity(intent);
        });

        chargeLayout.setOnClickListener(v -> {
            Intent intent = new Intent(PointMainActivity.this, PointChargeActivity.class);
            startActivity(intent);
        });

        refundLayout.setOnClickListener(v -> {
            //Intent intent = new Intent(PointMainActivity.this, PointRefundActivity.class);
           // startActivity(intent);
        });
    }
}
