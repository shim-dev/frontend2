package com.example.it_contest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.it_contest.network.FoodSearchResult;

import java.util.ArrayList;

public class SearchResultMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<FoodSearchResult> foodList =
                getIntent().getParcelableArrayListExtra("food_results");

        String initialQuery = getIntent().getStringExtra("query");
        EditText searchEditText = findViewById(R.id.searchEditText);
        if (initialQuery != null) searchEditText.setText(initialQuery);

        SearchResultAdapter adapter = new SearchResultAdapter(foodList, food -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_food", food);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
