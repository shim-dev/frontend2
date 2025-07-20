package com.example.it_contest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;
import com.example.it_contest.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp09Activity extends AppCompatActivity {

    private int value = 0;
    private TextView numLeft, numRight;
    private UserData userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_09);

        userData = (UserData) getIntent().getSerializableExtra("userData");

        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        numLeft = findViewById(R.id.num_left);
        numRight = findViewById(R.id.num_right);
        ImageButton btnUp = findViewById(R.id.btn_up);
        ImageButton btnDown = findViewById(R.id.btn_down);

        updateNumber();

        btnUp.setOnClickListener(v -> {
            value = (value + 1) % 100;
            updateNumber();
        });

        btnDown.setOnClickListener(v -> {
            value = (value - 1 + 100) % 100;
            updateNumber();
        });

        Button btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            if (userData != null) {
                userData.alcohol = value;
            }

            RetrofitClient.getApiService().registerUser(userData).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userEmail", userData.email);
                        editor.apply();

                        Intent intent = new Intent(SignUp09Activity.this, SignUp10Activity.class);
                        intent.putExtra("userData", userData);
                        startActivity(intent);
                    } else {
                        Log.e("REGISTER", "응답 실패: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("REGISTER", "통신 오류: " + t.getMessage());
                }
            });
        });
    }

    private void updateNumber() {
        int left = value / 10;
        int right = value % 10;
        numLeft.setText(String.valueOf(left));
        numRight.setText(String.valueOf(right));
    }
}
