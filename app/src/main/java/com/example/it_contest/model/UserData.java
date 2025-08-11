package com.example.it_contest.model;

import java.io.Serializable;

public class UserData implements Serializable {
    public String email;
    public String password;
    public String nickname;
    public String birthdate;
    public String gender;
    public int heightCm;
    public int weightKg;
    public String activityLevel;
    public int sleepHours;
    public int caffeine;
    public int alcohol;
    public int point; // 포인트 추가! 기본값 0

    // 생성자 (선택)
    public UserData() {}
}
