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

public class SignupActivity extends AppCompatActivity {
    // 도메인 선택 인덱스를 저장하는 변수
    final int[] selectedDomainIndex = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_01);

        // spinner 도메인 선택 파트
        Spinner spinnerDomain = findViewById(R.id.spinner_domain);
        String[] domains = {"도메인 입력", "naver.com", "google.com", "han.com"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, domains);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinnerDomain.setAdapter(adapter);

        // 기본 선택값을 "도메인 입력"으로
        spinnerDomain.setSelection(0);

        // 입력 칸 찾기
        EditText editEmail = findViewById(R.id.editText_email); // 이메일 입력칸 id
        EditText editPw = findViewById(R.id.editText_password); // 비밀번호 입력칸 id
        EditText editPw2 = findViewById(R.id.editText_password_confirm); // 비밀번호 재입력칸 id
        Button btnNext = findViewById(R.id.btn_next); // "다음" 버튼 id

        btnNext.setEnabled(false); // 처음엔 비활성화

        // 도메인 스피너 선택 리스너
        spinnerDomain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirstSelection = true; // 초기 진입 플래그

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDomainIndex[0] = position; // 현재 선택값 저장

                if (position == 0) {
                    if (!isFirstSelection) {
                        // "도메인 입력" 선택 시 강제로 naver.com으로 이동
                        spinnerDomain.setSelection(1);
                        selectedDomainIndex[0] = 1;
                    }
                }
                isFirstSelection = false;
                checkEnableButton(editEmail, editPw, editPw2, btnNext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 텍스트 변경 시 버튼 활성화/비활성화 체크
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEnableButton(editEmail, editPw, editPw2, btnNext);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editEmail.addTextChangedListener(textWatcher);
        editPw.addTextChangedListener(textWatcher);
        editPw2.addTextChangedListener(textWatcher);

        // 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        // 다음 버튼 클릭 시
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, SignUp02Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // 입력값 및 도메인 선택 여부로 다음 버튼 활성화 체크
    private void checkEnableButton(EditText editEmail, EditText editPw, EditText editPw2, Button btnNext) {
        String email = editEmail.getText().toString().trim();
        String pw = editPw.getText().toString().trim();
        String pw2 = editPw2.getText().toString().trim();

        boolean isEmailValid = !email.isEmpty();
        boolean isPwMatch = !pw.isEmpty() && pw.equals(pw2);
        boolean isPwValid = pw.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
        boolean isDomainSelected = selectedDomainIndex[0] != 0;

        boolean enable = isEmailValid && isPwMatch && isPwValid && isDomainSelected;
        btnNext.setEnabled(enable);

        // 비밀번호가 잘못되었을 경우 사용자에게 알려줌
        if (!pw.isEmpty() && !isPwValid) {
            editPw.setError("비밀번호는 영문, 숫자, 특수문자 포함 8자 이상이어야 합니다.");
        } else {
            editPw.setError(null); // 에러 해제
        }

        if (!pw2.isEmpty() && !pw.equals(pw2)) {
            editPw2.setError("비밀번호가 일치하지 않습니다");
        } else {
            editPw2.setError(null);
        }
    }
}
