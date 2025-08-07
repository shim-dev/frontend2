package com.example.it_contest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUploadActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private LinearLayout uploadArea;
    private ImageView imageView, iconView;
    private TextView uploadText;
    private Button uploadButton;
    private Uri selectedImageUri;
    private ImageButton backButton;


    private String challengeId;
    private int todayDay;
    private int goalSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        setupBottomNavigation("challenge");

        iconView = findViewById(R.id.image_preview_icon); // 아이콘 자체
        uploadText = findViewById(R.id.upload_text);
        uploadArea = findViewById(R.id.image_preview_area);           // 박스 전체 클릭 가능
        imageView = findViewById(R.id.image_preview);       // 실제 미리보기에 쓰일 이미지
        uploadButton = findViewById(R.id.upload_button);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        challengeId = getIntent().getStringExtra("challenge_id");
        todayDay = getIntent().getIntExtra("today_day", 1);
        goalSteps = getIntent().getIntExtra("goal_steps", 5000);

        // 업로드 박스를 클릭하면 이미지 선택창 열기
        uploadArea.setOnClickListener(v -> openFileChooser());


        // 업로드 버튼
        uploadButton.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    uploadImageToServer(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);

            // 이미지 미리보기 보이게
            imageView.setVisibility(View.VISIBLE);

            // 아이콘 & 텍스트 숨기기
            iconView.setVisibility(View.GONE);
            uploadText.setVisibility(View.GONE);
        }
    }



    private void uploadImageToServer(Bitmap bitmap) {
        // 비트맵을 Base64로 인코딩
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        // 서버로 전송할 데이터 구성
        JsonObject json = new JsonObject();
        json.addProperty("image", base64Image);
        json.addProperty("challenge_id", challengeId);
        json.addProperty("today_day", todayDay);
        json.addProperty("goal_steps", goalSteps);
        json.addProperty("nickname", getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("nickname", ""));

        ApiService apiService = RetrofitClient.getApiService();
        apiService.verifyChallengeImage(json).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean success = response.body().get("success").getAsBoolean();
                    if (success) {
                        Toast.makeText(ImageUploadActivity.this, "인증 성공!", Toast.LENGTH_SHORT).show();

                        //  인증 성공 여부를 결과로 전달
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("verified", true);
                        setResult(Activity.RESULT_OK, resultIntent);

                        finish();  // ChallengeVerificationActivity로 돌아감
                    } else {
                        Toast.makeText(ImageUploadActivity.this, "인증 실패: 걸음 수 부족", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ImageUploadActivity.this, "서버 응답 오류", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(ImageUploadActivity.this, "서버 연결 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
