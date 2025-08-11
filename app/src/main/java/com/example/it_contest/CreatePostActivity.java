package com.example.it_contest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etContent, etTime;
    private TextInputLayout tilTime;
    private ChipGroup chipGroupCategory;
    private MaterialButtonToggleGroup difficultyGroup;
    private MaterialButton btnSubmit;
    private ImageView imgPreview;
    private Uri selectedImageUri = null;

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
        setupCategoryChips();
        setupDifficulty();
        setupImagePicker();
        setupSubmit();
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        etTime = findViewById(R.id.etTime);
        tilTime = findViewById(R.id.tilTime);
        chipGroupCategory = findViewById(R.id.chipGroupCategory);
        difficultyGroup = findViewById(R.id.difficultyGroup);
        btnSubmit = findViewById(R.id.btnSubmit);
        imgPreview = findViewById(R.id.imgPreview);
    }

    private void setupCategoryChips() {
        // 다중 선택 가능
        chipGroupCategory.setSingleSelection(false);
        // XML에 이미 Chip이 정의돼 있어도 동작함. (추가로 동적 생성 시 아래 예시)
        // addChip("#태국", false);
    }

    private void addChip(String text, boolean isChecked) {
        Chip chip = new Chip(this);
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChecked(isChecked);
        chip.setChipBackgroundColorResource(R.color.chip_bg_selector);
        chipGroupCategory.addView(chip);
    }

    private void setupDifficulty() {
        // 기본값: 중
        difficultyGroup.check(R.id.btnDiffMid);
        difficultyGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            // 단일선택 모드 유지 (XML에서 singleSelection=true)
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

            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String content = etContent.getText() != null ? etContent.getText().toString().trim() : "";
            int minutes = Integer.parseInt(etTime.getText().toString().trim());
            List<String> categories = getSelectedCategories();
            String difficulty = getSelectedDifficulty();

            // TODO: 여기서 서버 전송(Volley/Retrofit) 로직 연결
            // 예) CreatePostRequest req = new CreatePostRequest(title, content, categories, minutes, difficulty, selectedImageUri);

            Toast.makeText(this, "작성 완료 (로컬 검증 통과)", Toast.LENGTH_SHORT).show();
            finish();
        });
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

        if (difficultyGroup.getCheckedButtonId() == -1) {
            Toast.makeText(this, "난이도를 선택해 주세요", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        return ok;
    }

    private List<String> getSelectedCategories() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < chipGroupCategory.getChildCount(); i++) {
            Chip c = (Chip) chipGroupCategory.getChildAt(i);
            if (c.isChecked()) list.add(c.getText().toString().replace("#", ""));
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
}
