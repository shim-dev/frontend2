package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.model.UserData;

import java.util.ArrayList;

public class SignUp03Activity extends AppCompatActivity {
    private UserData userData; // ✅ 전달받은 객체 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_03);

        // ✅ 이전 화면에서 전달받은 UserData
        userData = (UserData) getIntent().getSerializableExtra("userData");

        // 1. 뒤로 가기
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(v -> finish());

        // 2. 다음 버튼
        Button buttonNext = findViewById(R.id.btn_next);
        buttonNext.setEnabled(false);

        // 3. Spinner 연결
        Spinner spinnerYear = findViewById(R.id.spinner_year);
        Spinner spinnerMonth = findViewById(R.id.spinner_month);
        Spinner spinnerDay = findViewById(R.id.spinner_day);

        // 3-1. 년도 설정
        ArrayList<String> years = new ArrayList<>();
        years.add("YYYY");
        for (int i = 1940; i <= 2025; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // 3-2. 월 설정
        ArrayList<String> months = new ArrayList<>();
        months.add("MM");
        for (int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // 3-3. 일 설정
        setDayAdapter(spinnerDay, 31);

        // Spinner 리스너
        AdapterView.OnItemSelectedListener yearMonthListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateDays(spinnerYear, spinnerMonth, spinnerDay);
                checkAndToggleNextButton(spinnerYear, spinnerMonth, spinnerDay, buttonNext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerYear.setOnItemSelectedListener(yearMonthListener);
        spinnerMonth.setOnItemSelectedListener(yearMonthListener);

        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkAndToggleNextButton(spinnerYear, spinnerMonth, spinnerDay, buttonNext);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ✅ 다음 버튼 클릭 시 생년월일 저장 및 다음 화면으로 이동
        buttonNext.setOnClickListener(v -> {
            String year = (String) spinnerYear.getSelectedItem();
            String month = (String) spinnerMonth.getSelectedItem();
            String day = (String) spinnerDay.getSelectedItem();

            // YYYY-MM-DD 형태로 저장
            String birthdate = year + "-" + month + "-" + day;
            userData.birthdate = birthdate;

            Intent intent = new Intent(SignUp03Activity.this, SignUp04Activity.class);
            intent.putExtra("userData", userData);
            startActivity(intent);
        });
    }

    // ===== 유틸 함수 =====

    private void setDayAdapter(Spinner spinnerDay, int lastDay) {
        ArrayList<String> days = new ArrayList<>();
        days.add("DD");
        for (int i = 1; i <= lastDay; i++) {
            days.add(String.format("%02d", i));
        }
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private void updateDays(Spinner spinnerYear, Spinner spinnerMonth, Spinner spinnerDay) {
        String yearStr = (String) spinnerYear.getSelectedItem();
        String monthStr = (String) spinnerMonth.getSelectedItem();
        int year, month;
        try {
            year = Integer.parseInt(yearStr);
            month = Integer.parseInt(monthStr);
        } catch (Exception e) {
            setDayAdapter(spinnerDay, 31);
            return;
        }

        int lastDay;
        switch (month) {
            case 2:
                lastDay = isLeapYear(year) ? 29 : 28;
                break;
            case 4: case 6: case 9: case 11:
                lastDay = 30;
                break;
            default:
                lastDay = 31;
        }
        setDayAdapter(spinnerDay, lastDay);
    }

    private void checkAndToggleNextButton(Spinner spinnerYear, Spinner spinnerMonth, Spinner spinnerDay, Button buttonNext) {
        String year = (String) spinnerYear.getSelectedItem();
        String month = (String) spinnerMonth.getSelectedItem();
        String day = (String) spinnerDay.getSelectedItem();
        boolean isAllSelected = !year.equals("YYYY") && !month.equals("MM") && !day.equals("DD");
        buttonNext.setEnabled(isAllSelected);
    }
}
