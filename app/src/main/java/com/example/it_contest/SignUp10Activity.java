package com.example.it_contest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SignUp10Activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_10); // sign_up_09.xml 연결

        // 시작하기 버튼 연결
        //Todo "한비 메인 페이지 클래스 이름" 에다가 ex) MainPageActivity.class 를 넣어 주면 됩니다
        // Intent intent = new Intent(SignUp10Activity.this, MainPageActivity.class);
//        Button btnNext = findViewById(R.id.btn_next);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 메인 페이지로 이동 (MainActivity로 예시)
//                Intent intent = new Intent(SignUp10Activity.this, 한비 메인 페이지 클래스 이름);
//                startActivity(intent);
//                finish(); // 뒤로가기 시 회원가입 완료 페이지로 안 돌아오게
//            }
//        });
    }
}
