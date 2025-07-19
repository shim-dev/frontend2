package com.example.it_contest;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PointHistoryActivity extends BaseActivity
 {
    private List<PointHistoryItem> fullItemList;
    private RecyclerView recyclerView;
    private PointHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_history);

        // 하단바 세팅
        setupBottomNavigation("challenge");

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> onBackPressed());

        recyclerView = findViewById(R.id.point_history_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fullItemList = new ArrayList<>();
        fullItemList.add(new PointHistoryItem("챌린지 참여", "-50P", "2025.07.01  하루 500보 걷기", "사용(700P)"));
        fullItemList.add(new PointHistoryItem("챌린지 성공금", "+50P", "2025.06.30  하루 500보 걷기", "적립(750P)"));
        fullItemList.add(new PointHistoryItem("포인트 충전", "+500P", "2025.06.28  500P 충전", "적립(700P)"));

        adapter = new PointHistoryAdapter(fullItemList);
        recyclerView.setAdapter(adapter);

        TextView tabAll = findViewById(R.id.tab_all);
        TextView tabEarn = findViewById(R.id.tab_earn);
        TextView tabUse = findViewById(R.id.tab_use);

        tabAll.setOnClickListener(v -> {
            adapter.updateData(fullItemList);
            setTabStyle(tabAll, tabEarn, tabUse);
        });

        tabEarn.setOnClickListener(v -> {
            List<PointHistoryItem> earnList = new ArrayList<>();
            for (PointHistoryItem item : fullItemList) {
                if (item.getPoint().trim().startsWith("+")) earnList.add(item);
            }
            adapter.updateData(earnList);
            setTabStyle(tabEarn, tabAll, tabUse);
        });

        tabUse.setOnClickListener(v -> {
            List<PointHistoryItem> useList = new ArrayList<>();
            for (PointHistoryItem item : fullItemList) {
                if (item.getPoint().trim().startsWith("-")) useList.add(item);
            }
            adapter.updateData(useList);
            setTabStyle(tabUse, tabAll, tabEarn);
        });
    }


    private void setTabStyle(TextView selected, TextView other1, TextView other2) {
        selected.setTextColor(getResources().getColor(android.R.color.black));
        selected.setTypeface(null, android.graphics.Typeface.BOLD);

        other1.setTextColor(android.graphics.Color.parseColor("#66000000"));
        other1.setTypeface(null, android.graphics.Typeface.NORMAL);

        other2.setTextColor(android.graphics.Color.parseColor("#66000000"));
        other2.setTypeface(null, android.graphics.Typeface.NORMAL);
    }
}
