package com.example.it_contest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointRefundActivity extends BaseActivity {

    private Spinner bankSpinner;
    private EditText accountNumberInput, accountHolderInput, refundAmountInput;
    private Button confirmButton, useAllButton;
    private TextView availablePointText, myPointText;

    private int currentPoints = 0;
    private String nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_refund);
        setupBottomNavigation("my");

        ImageButton backButton = findViewById(R.id.back_button); // ← 너 layout에 버튼 id가 back_button이어야 함
        backButton.setOnClickListener(v -> onBackPressed());


        // 바인딩
        bankSpinner = findViewById(R.id.bank_spinner);
        accountNumberInput = findViewById(R.id.account_number);
        accountHolderInput = findViewById(R.id.account_holder);
        refundAmountInput = findViewById(R.id.edit_refund_amount);
        confirmButton = findViewById(R.id.confirm_button);
        useAllButton = findViewById(R.id.use_all_button);
        myPointText = findViewById(R.id.text_my_point);
        availablePointText = findViewById(R.id.available_point);


        // Spinner 설정
        String[] banks = {"신한은행", "국민은행", "우리은행", "하나은행", "카카오뱅크", "토스뱅크"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bankSpinner.setAdapter(adapter);

        // 닉네임 가져오기
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        nickname = prefs.getString("nickname", null);

        // 포인트 불러오기
        loadUserPoint();

        // 전액 사용 버튼
        useAllButton.setOnClickListener(v -> refundAmountInput.setText(String.valueOf(currentPoints)));

        // 신청 버튼
        confirmButton.setOnClickListener(v -> submitRefund());
    }

    private void loadUserPoint() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUserInfo(nickname).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPoints = response.body().get("point").getAsInt();
                    availablePointText.setText(currentPoints + "P");
                    myPointText.setText(currentPoints + "P");

                    // 버튼 활성/비활성 + 색상 변경
                    if (currentPoints < 1000) {
                        confirmButton.setEnabled(false);
                        confirmButton.setBackgroundTintList(ColorStateList.valueOf(
                                ContextCompat.getColor(PointRefundActivity.this, android.R.color.darker_gray)));
                    } else {
                        confirmButton.setEnabled(true);
                        confirmButton.setBackgroundTintList(ColorStateList.valueOf(
                                ContextCompat.getColor(PointRefundActivity.this, R.color.black)));
                    }

                } else {
                    Toast.makeText(PointRefundActivity.this, "포인트 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(PointRefundActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void submitRefund() {
        String bank = bankSpinner.getSelectedItem().toString();
        String accountNumber = accountNumberInput.getText().toString().trim();
        String accountHolder = accountHolderInput.getText().toString().trim();
        String refundAmountStr = refundAmountInput.getText().toString().trim();

        if (accountNumber.isEmpty() || accountHolder.isEmpty() || refundAmountStr.isEmpty()) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int refundAmount = Integer.parseInt(refundAmountStr);
        if (refundAmount > currentPoints) {
            Toast.makeText(this, "환급 포인트가 보유 포인트보다 많습니다.", Toast.LENGTH_SHORT).show();
            return;
        }


        JsonObject data = new JsonObject();
        data.addProperty("nickname", nickname);
        data.addProperty("bank", bank);
        data.addProperty("account_number", accountNumber);
        data.addProperty("account_holder", accountHolder);
        data.addProperty("refund_amount", refundAmount);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.applyRefund(data).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PointRefundActivity.this, "환급 신청 완료", Toast.LENGTH_SHORT).show();

                    //  변경된 포인트 계산해서 돌려줌
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("new_point", currentPoints - refundAmount);
                    setResult(RESULT_OK, resultIntent);

                    finish();
                } else {
                    Toast.makeText(PointRefundActivity.this, "환급 신청 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(PointRefundActivity.this, "네트워크 오류", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
