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
    private TextView btnAddStep, btnAddIngredient, btnSubmit; // ✅ btnSubmit은 TextView이므로 타입 수정
    private ImageView imgPreview;
    private Uri selectedImageUri = null;
    private ImageButton btnBack; // ✅ 클래스 멤버 변수 선언

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
        // ✅ 이제 btnBack이 초기화되었으므로 NullPointerException 없이 호출 가능
        btnBack.setOnClickListener(v -> finish());

        setupCategoryButtons();
        setupImagePicker();
        setupDynamicFields();
        setupSubmit();
    }

    private void initViews() {
        // ✅ 클래스 멤버 변수에 직접 할당
        btnBack = findViewById(R.id.btnBack);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etTime = findViewById(R.id.etTime);
        tilTime = findViewById(R.id.tilTime);
        etServing = findViewById(R.id.etServing);
        difficultyGroup = findViewById(R.id.difficultyGroup);
        btnSubmit = findViewById(R.id.btnSubmit); // ✅ MaterialButton이 아닌 TextView
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

            // ✅ 배경 셀렉터를 적용하는 올바른 방법
            button.setBackgroundResource(R.drawable.button_selector);
            // ✅ 글자색 셀렉터를 적용하는 올바른 방법
            button.setTextColor(ContextCompat.getColorStateList(this, R.color.chip_bg_selector));

            // LayoutParams 설정
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
        // difficultyGroup.check(R.id.btnDiffMid); // XML에서 기본값 설정하므로 필요 없음
        difficultyGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            // 단일선택 모드 유지
        });
    }

    private void setupImagePicker() {
        // findViewById(R.id.areaAddPhoto).setOnClickListener(v -> {
        // imagePicker.launch("image/*");
        // });
    }

    private void setupSubmit() {
        // btnSubmit.setOnClickListener(v -> {
        // ...
        // });
    }

    private void uploadImageAndCreateRecipe(String name, String desc, String time, List<String> keywords, String level, int serving, List<String> steps, List<String> ingredients) {
        // btnSubmit.setEnabled(false);
        // btnSubmit.setText("업로드 중...");

        // ... (Firebase Storage 로직) ...
        Toast.makeText(this, "서버 전송 로직 구현 필요", Toast.LENGTH_SHORT).show();
    }

    private void createRecipe(String name, String desc, String time, List<String> keywords, String level, List<String> imageUrls, int serving, List<String> steps, List<String> ingredients) {
        // CreateRecipeRequest recipeRequest = new CreateRecipeRequest(name, desc, keywords, time, level, imageUrls, serving, steps, ingredients);
        // ... (Retrofit 로직) ...
        Toast.makeText(this, "서버 전송 로직 구현 필요", Toast.LENGTH_SHORT).show();
    }

    private void resetSubmitButton() {
        btnSubmit.setEnabled(true);
        btnSubmit.setText("작성하기"); // ✅ 버튼 텍스트를 "작성하기"로 통일
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
        if (id == R.id.btnDiffLow) return "LOW";
        if (id == R.id.btnDiffMid) return "MID";
        if (id == R.id.btnDiffHigh) return "HIGH";
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
}