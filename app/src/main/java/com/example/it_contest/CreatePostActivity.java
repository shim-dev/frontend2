package com.example.it_contest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.content.SharedPreferences;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.it_contest.model.CreateRecipeRequest;
import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.flexbox.FlexboxLayout.LayoutParams;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etContent, etTime, etServing;
    private TextInputLayout tilTime;
    private FlexboxLayout categoryContainer;
    private MaterialButtonToggleGroup difficultyGroup;
    private TextView btnAddStep, btnAddIngredient, btnSubmit;
    private ImageView imgPreview;
    private Uri selectedImageUri = null;
    private ImageButton btnBack;

    private LinearLayout stepsContainer, ingredientsContainer;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgPreview.setImageURI(uri);
                    imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        initViews();
        btnBack.setOnClickListener(v -> finish());
        setupCategoryButtons();
        setupImagePicker();
        setupDynamicFields();
        setupSubmit();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etTime = findViewById(R.id.etTime);
        tilTime = findViewById(R.id.tilTime);
        etServing = findViewById(R.id.etServing);
        difficultyGroup = findViewById(R.id.difficultyGroup);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);

        categoryContainer = findViewById(R.id.categoryContainer);
        stepsContainer = findViewById(R.id.stepsContainer);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);

        btnAddStep = findViewById(R.id.btnAddStep);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
    }

    private void setupCategoryButtons() {
        String[] categories = {"한식", "양식", "중식", "일식", "동남아시아", "퓨전", "채식"};
        for (String cat : categories) {
            Button button = new Button(this);
            button.setText("#" + cat);
            button.setTag(cat);
            button.setTextSize(14);

            button.setBackgroundResource(R.drawable.button_selector);
            button.setTextColor(ContextCompat.getColorStateList(this, R.color.chip_bg_selector));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 8, 8);
            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
                v.setSelected(!v.isSelected());
            });
            categoryContainer.addView(button);
        }
    }

    private void setupDynamicFields() {
        btnAddStep.setOnClickListener(v -> addDynamicInputField(stepsContainer, "레시피 단계", "단계 추가"));
        btnAddIngredient.setOnClickListener(v -> addDynamicInputField(ingredientsContainer, "재료", "재료 추가"));
    }

    private void addDynamicInputField(LinearLayout container, String hintText, String buttonText) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        rowLayout.setPadding(0, 8, 0, 8);

        TextInputLayout textInputLayout = new TextInputLayout(this);
        textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        textInputLayout.setHint(hintText);

        TextInputEditText editText = new TextInputEditText(textInputLayout.getContext());
        textInputLayout.addView(editText);

        ImageView removeButton = new ImageView(this);
        removeButton.setImageResource(R.drawable.ic_delete);
        removeButton.setPadding(16, 16, 16, 16);
        removeButton.setOnClickListener(v -> container.removeView(rowLayout));

        rowLayout.addView(textInputLayout);
        rowLayout.addView(removeButton);

        container.addView(rowLayout);
    }

    private void setupDifficulty() {
        difficultyGroup.check(R.id.btnDiffMid);
        difficultyGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
        });
    }

    private void setupImagePicker() {
        findViewById(R.id.areaAddPhoto).setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });
    }

    private void setupSubmit() {
        btnSubmit.setOnClickListener(v -> {
            if (!validate()) return;

            String name = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String desc = etContent.getText() != null ? etContent.getText().toString().trim() : "";
            String timeStr = etTime.getText().toString().trim();
            List<String> keywords = getSelectedCategories();
            String level = getSelectedDifficulty();
            int serving = Integer.parseInt(etServing.getText().toString().trim());

            List<String> steps = getDynamicInputValues(stepsContainer);
            List<String> ingredients = getDynamicInputValues(ingredientsContainer);

            if (selectedImageUri != null) {
                uploadImageAndCreateRecipe(name, desc, timeStr, keywords, level, serving, steps, ingredients);
            } else {
                createRecipe(name, desc, timeStr, keywords, level, "", serving, steps, ingredients); // ✅ 빈 문자열로 변경
            }
        });
    }

    private void uploadImageAndCreateRecipe(String name, String desc, String time, List<String> keywords, String level, int serving, List<String> steps, List<String> ingredients) {
        btnSubmit.setEnabled(false);
        btnSubmit.setText("업로드 중...");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("recipe_images/" + UUID.randomUUID().toString());

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString(); // ✅ String으로 변경
                        createRecipe(name, desc, time, keywords, level, imageUrl, serving, steps, ingredients);
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, "이미지 URL 가져오기 실패", Toast.LENGTH_SHORT).show();
                        resetSubmitButton();
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "이미지 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("CreatePostActivity", "Image upload failed", e);
                    resetSubmitButton();
                });
    }

    // ✅ 메서드 시그니처 변경: List<String> imageUrl → String imageUrl
    private void createRecipe(String name, String desc, String time, List<String> keywords, String level, String imageUrl, int serving, List<String> steps, List<String> ingredients) {
        CreateRecipeRequest recipeRequest = new CreateRecipeRequest(name, desc, keywords, time, level, imageUrl, serving, steps, ingredients);

        ApiService apiService = RetrofitClient.getApiService();
        Call<JsonObject> call = apiService.createRecipe(recipeRequest);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CreatePostActivity.this, "레시피가 성공적으로 작성되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "알 수 없는 오류";
                        Toast.makeText(CreatePostActivity.this, "레시피 작성 실패: " + errorBody, Toast.LENGTH_LONG).show();
                        Log.e("CreatePostActivity", "Post creation failed: " + response.code() + " - " + errorBody);
                    } catch (Exception e) {
                        Toast.makeText(CreatePostActivity.this, "레시피 작성 실패: 오류 처리 중 예외 발생", Toast.LENGTH_LONG).show();
                    }
                    resetSubmitButton();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(CreatePostActivity.this, "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CreatePostActivity", "Network error", t);
                resetSubmitButton();
            }
        });
    }

    private void resetSubmitButton() {
        btnSubmit.setEnabled(true);
        btnSubmit.setText("작성하기");
    }

    private boolean validate() {
        boolean ok = true;

        if (TextUtils.isEmpty(etTitle.getText())) {
            etTitle.setError("제목을 입력해 주세요");
            ok = false;
        } else {
            etTitle.setError(null);
        }

        if (TextUtils.isEmpty(etContent.getText())) {
            etContent.setError("내용을 입력해 주세요");
            ok = false;
        } else {
            etContent.setError(null);
        }

        String timeStr = etTime.getText() == null ? "" : etTime.getText().toString().trim();
        if (timeStr.isEmpty()) {
            tilTime.setError("소요 시간을 입력해 주세요");
            ok = false;
        } else {
            try {
                int m = Integer.parseInt(timeStr);
                if (m <= 0) {
                    tilTime.setError("1분 이상 입력해 주세요");
                    ok = false;
                } else tilTime.setError(null);
            } catch (NumberFormatException e) {
                tilTime.setError("숫자만 입력해 주세요");
                ok = false;
            }
        }

        if (getDynamicInputValues(ingredientsContainer).isEmpty()) {
            Toast.makeText(this, "재료를 하나 이상 입력해 주세요", Toast.LENGTH_SHORT).show();
            ok = false;
        }
        if (getDynamicInputValues(stepsContainer).isEmpty()) {
            Toast.makeText(this, "조리 순서를 하나 이상 입력해 주세요", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        if (difficultyGroup.getCheckedButtonId() == -1) {
            Toast.makeText(this, "난이도를 선택해 주세요", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        return ok;
    }

    private List<String> getSelectedCategories() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View child = categoryContainer.getChildAt(i);
            if (child instanceof Button && child.isSelected()) {
                list.add(((Button) child).getTag().toString());
            }
        }
        return list;
    }

    private String getSelectedDifficulty() {
        int id = difficultyGroup.getCheckedButtonId();
        if (id == R.id.btnDiffLow) return "하";
        if (id == R.id.btnDiffMid) return "중";
        if (id == R.id.btnDiffHigh) return "상";
        return "";
    }

    private List<String> getDynamicInputValues(LinearLayout container) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof LinearLayout) {
                TextInputLayout til = (TextInputLayout) ((LinearLayout) child).getChildAt(0);
                if (til.getEditText() != null && !TextUtils.isEmpty(til.getEditText().getText())) {
                    values.add(til.getEditText().getText().toString().trim());
                }
            }
        }
        return values;
    }

    private String getLoggedInUserNickname() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return prefs.getString("nickname", "shim"); // 닉네임이 없으면 'shim' 반환
    }
}