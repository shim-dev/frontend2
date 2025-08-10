package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Button;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.Spinner;

import com.example.it_contest.model.UserData;
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {
    final int[] selectedDomainIndex = {0};
    private UserData userData = new UserData();

    private EditText editEmail, editPw, editPw2;
    private Button btnNext;
    private Spinner spinnerDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_01);

        spinnerDomain = findViewById(R.id.spinner_domain);
        String[] domains = {"도메인 입력", "naver.com", "google.com", "han.com"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, domains);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerDomain.setAdapter(adapter);
        spinnerDomain.setSelection(0);

        editEmail = findViewById(R.id.editText_email);
        editPw = findViewById(R.id.editText_password);
        editPw2 = findViewById(R.id.editText_password_confirm);
        btnNext = findViewById(R.id.btn_next);
        btnNext.setEnabled(false);

        // 도메인 선택 이벤트
        spinnerDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDomainIndex[0] = position;
                if (position == 0 && !isFirstSelection) {
                    spinnerDomain.setSelection(1);
                    selectedDomainIndex[0] = 1;
                }
                isFirstSelection = false;

                String email = editEmail.getText().toString().trim();
                String domain = (String) spinnerDomain.getSelectedItem();
                checkEmailDuplication(email, domain);
                checkEnableButton();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 텍스트 변화 감지
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String email = editEmail.getText().toString().trim();
                String domain = (String) spinnerDomain.getSelectedItem();
                checkEmailDuplication(email, domain);
                checkEnableButton();
            }
        };

        editEmail.addTextChangedListener(textWatcher);
        editPw.addTextChangedListener(textWatcher);
        editPw2.addTextChangedListener(textWatcher);

        // 뒤로 가기
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        // 다음 버튼 클릭
        btnNext.setOnClickListener(v -> {
            String emailInput = editEmail.getText().toString().trim();
            String pwInput = editPw.getText().toString().trim();
            String domain = (String) spinnerDomain.getSelectedItem();

            userData.email = emailInput + "@" + domain;
            userData.password = pwInput;

            Intent intent = new Intent(SignupActivity.this, SignUp02Activity.class);
            intent.putExtra("userData", userData);
            startActivity(intent);
            finish();
        });
    }

    // 이메일 중복 검사
    private void checkEmailDuplication(String localEmail, String domain) {
        if (localEmail.isEmpty() || domain.equals("도메인 입력")) return;

        String fullEmail = localEmail + "@" + domain;
        JsonObject json = new JsonObject();
        json.addProperty("email", fullEmail);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.checkEmail(json).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean exists = response.body().get("exists").getAsBoolean();
                    if (exists) {
                        editEmail.setError("이미 사용 중인 이메일입니다.");
                        btnNext.setEnabled(false);
                    } else {
                        editEmail.setError(null);
                        checkEnableButton(); // 이메일 중복 아님 → 전체 조건 다시 확인
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                editEmail.setError("서버 오류: 중복 검사 실패");
                btnNext.setEnabled(false);
            }
        });
    }

    // 입력값 유효성 전체 확인
    private void checkEnableButton() {
        String email = editEmail.getText().toString().trim();
        String pw = editPw.getText().toString().trim();
        String pw2 = editPw2.getText().toString().trim();
        boolean isEmailValid = !email.isEmpty();
        boolean isPwMatch = !pw.isEmpty() && pw.equals(pw2);
        boolean isPwValid = pw.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
        boolean isDomainSelected = selectedDomainIndex[0] != 0;

        boolean enable = isEmailValid && isPwMatch && isPwValid && isDomainSelected && editEmail.getError() == null;
        btnNext.setEnabled(enable);

        if (!pw.isEmpty() && !isPwValid) {
            editPw.setError("비밀번호는 영문, 숫자, 특수문자 포함 8자 이상이어야 합니다.");
        } else {
            editPw.setError(null);
        }

        if (!pw2.isEmpty() && !pw.equals(pw2)) {
            editPw2.setError("비밀번호가 일치하지 않습니다");
        } else {
            editPw2.setError(null);
        }
    }
}
