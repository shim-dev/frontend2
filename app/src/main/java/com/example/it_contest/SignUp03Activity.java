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

import java.util.ArrayList;

public class SignUp03Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_03); // 레이아웃 연결

        // 1. 뒤로 가기 버튼
        ImageView btnBack = findViewById(R.id.logo);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 복귀
            }
        });

        // 2. 다음 버튼
        Button buttonNext = findViewById(R.id.btn_next);
        buttonNext.setEnabled(false); // 처음엔 비활성화

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SignUp04Activity로 이동
                Intent intent = new Intent(SignUp03Activity.this, SignUp04Activity.class);
                startActivity(intent);
            }
        });

        // 3. 생년월일 Spinner 연결
        Spinner spinnerYear = findViewById(R.id.spinner_year);
        Spinner spinnerMonth = findViewById(R.id.spinner_month);
        Spinner spinnerDay = findViewById(R.id.spinner_day);

        // 3-1. 년도(1940~2025)
        ArrayList<String> years = new ArrayList<>();
        years.add("YYYY");
        for(int i = 1940; i <= 2025; i++) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // 3-2. 월(01~12)
        ArrayList<String> months = new ArrayList<>();
        months.add("MM");
        for(int i = 1; i <= 12; i++) {
            months.add(String.format("%02d", i));
        }
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // 3-3. 일(01~31) - 최초 31일까지 설정
        setDayAdapter(spinnerDay, 31);

        // 4. Spinner 변화시 버튼 활성화 여부 및 일(day) 자동 업데이트 (윤년 포함)
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
    }

    // ====== 함수 정의부 ======

    // 일(day) Spinner 세팅
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

    // 윤년 체크
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    // 년/월 선택에 따라 일(day) 자동 업데이트
    private void updateDays(Spinner spinnerYear, Spinner spinnerMonth, Spinner spinnerDay) {
        String yearStr = (String) spinnerYear.getSelectedItem();
        String monthStr = (String) spinnerMonth.getSelectedItem();
        int year, month;
        try {
            year = Integer.parseInt(yearStr);
            month = Integer.parseInt(monthStr);
        } catch (Exception e) {
            // "YYYY" 또는 "MM"이 선택된 경우
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

    // 다음 버튼 활성화/비활성화 체크
    private void checkAndToggleNextButton(Spinner spinnerYear, Spinner spinnerMonth, Spinner spinnerDay, Button buttonNext) {
        String year = (String) spinnerYear.getSelectedItem();
        String month = (String) spinnerMonth.getSelectedItem();
        String day = (String) spinnerDay.getSelectedItem();
        boolean isAllNumeric = !year.equals("YYYY") && !month.equals("MM") && !day.equals("DD");
        buttonNext.setEnabled(isAllNumeric);
    }
}
