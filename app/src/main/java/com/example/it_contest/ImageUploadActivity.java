package com.example.it_contest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    // 로그인 닉네임
    private String nickname;

    // 폴링용 핸들러/러너블(메모리 누수 방지 위해 필드로 보관)
    private Handler pollingHandler;
    private Runnable pollingRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        setupBottomNavigation("challenge");

        iconView = findViewById(R.id.image_preview_icon);
        uploadText = findViewById(R.id.upload_text);
        uploadArea = findViewById(R.id.image_preview_area);
        imageView = findViewById(R.id.image_preview);
        uploadButton = findViewById(R.id.upload_button);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> finish());

        challengeId = getIntent().getStringExtra("challenge_id");
        todayDay = getIntent().getIntExtra("today_day", 1);
        goalSteps = getIntent().getIntExtra("goal_steps", 5000);
        nickname = getSharedPreferences("UserPrefs", MODE_PRIVATE).getString("nickname", "");

        uploadArea.setOnClickListener(v -> openFileChooser());

        uploadButton.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                Toast.makeText(ImageUploadActivity.this, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                uploadImageToServer(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ImageUploadActivity.this, "이미지 로드 실패", Toast.LENGTH_SHORT).show();
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

            imageView.setVisibility(View.VISIBLE);
            iconView.setVisibility(View.GONE);
            uploadText.setVisibility(View.GONE);
        }
    }

    private void uploadImageToServer(Bitmap bitmap) {
        // 전송 최적화: 1080px 너비로 스케일 + JPEG 85%
        String base64Image = bitmapToBase64Compressed(bitmap, 1080, 85);

        JsonObject json = new JsonObject();
        json.addProperty("image", base64Image);
        json.addProperty("challenge_id", challengeId);
        json.addProperty("today_day", todayDay);
        json.addProperty("goal_steps", goalSteps);
        json.addProperty("nickname", nickname);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.verifyChallengeImage(json).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ImageUploadActivity.this, "서버 응답 오류", Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean success = response.body().get("success").getAsBoolean();
                if (success) {
                    Toast.makeText(ImageUploadActivity.this, "인증 성공!", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("verified", true);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(ImageUploadActivity.this, "인증 실패: 걸음 수 부족", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t instanceof java.net.SocketTimeoutException) {
                    // 타임아웃 → 서버는 처리했을 수 있으니 폴링으로 확인
                    startStatusPolling(challengeId, nickname, todayDay);
                } else {
                    Toast.makeText(ImageUploadActivity.this, "업로드 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 인증 결과 폴링: 2.5초 간격으로 최대 6회(≈15초)
     */
    private void startStatusPolling(String challengeId, String nickname, int todayDay) {
        ApiService api = RetrofitClient.getApiService();

        pollingHandler = new Handler(Looper.getMainLooper());
        final int[] tries = {0};
        final int maxTries = 6;
        final long interval = 2500L;

        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                api.getChallengeVerificationStatus(challengeId, nickname)
                        .enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> res) {
                                if (res.isSuccessful() && res.body() != null) {
                                    JsonArray arr = res.body().getAsJsonArray("certified_days");
                                    boolean ok = false;
                                    for (JsonElement e : arr) {
                                        try {
                                            if (Integer.parseInt(e.getAsString()) == todayDay) {
                                                ok = true;
                                                break;
                                            }
                                        } catch (NumberFormatException ignore) {}
                                    }
                                    if (ok) {
                                        Intent i = new Intent();
                                        i.putExtra("verified", true);
                                        setResult(RESULT_OK, i);
                                        finish();
                                        return;
                                    }
                                }
                                if (++tries[0] < maxTries) {
                                    pollingHandler.postDelayed(pollingRunnable, interval);
                                } else {
                                    Toast.makeText(ImageUploadActivity.this, "인증 처리 지연 중입니다. 잠시 후 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                if (++tries[0] < maxTries) {
                                    pollingHandler.postDelayed(pollingRunnable, interval);
                                } else {
                                    Toast.makeText(ImageUploadActivity.this, "서버 통신 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        };

        pollingHandler.post(pollingRunnable);
    }

    @Override
    protected void onDestroy() {
        if (pollingHandler != null && pollingRunnable != null) {
            pollingHandler.removeCallbacks(pollingRunnable);
        }
        super.onDestroy();
    }

    // --------- 유틸: 비트맵 압축/인코딩 ---------
    private String bitmapToBase64Compressed(Bitmap src, int targetWidth, int jpegQuality) {
        if (src == null) return "";

        int w = src.getWidth();
        int h = src.getHeight();
        if (w > targetWidth) {
            float scale = targetWidth / (float) w;
            Matrix m = new Matrix();
            m.postScale(scale, scale);
            src = Bitmap.createBitmap(src, 0, 0, w, h, m, true);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.JPEG, jpegQuality, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.NO_WRAP); // 줄바꿈 제거
    }
}