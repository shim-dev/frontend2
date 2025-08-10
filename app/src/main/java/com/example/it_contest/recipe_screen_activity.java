package com.example.it_contest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.HorizontalScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.it_contest.model.Recipe;

import java.util.List;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

public class recipe_screen_activity extends AppCompatActivity {

    private ImageView btnBack, btnBookmark, btnShare, imgRecipe, imgFood;
    private TextView timeInfo, levelInfo, servingInfo;
    private TextView tvTitle, tvSteps, tvBookTitle;
    private LinearLayout layoutIngredients, layoutBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_screen);

        // 뷰 연결
        btnBack = findViewById(R.id.btn_back);
        btnBookmark = findViewById(R.id.btn_bookmark);
        btnShare = findViewById(R.id.btn_share);
        imgRecipe = findViewById(R.id.img_recipe);
        tvTitle = findViewById(R.id.tv_title);
        tvSteps = findViewById(R.id.tv_steps);
        layoutIngredients = findViewById(R.id.layout_ingredients);
        layoutBook = findViewById(R.id.layout_youtube); // XML ID 유지
        imgFood = findViewById(R.id.img_food);
        tvBookTitle = findViewById(R.id.tv_book_title);

        // ✅ 시간·난이도·인분 View 연결
        timeInfo = findViewById(R.id.time_info);
        levelInfo = findViewById(R.id.level_info);
        servingInfo = findViewById(R.id.serving_info);
        HorizontalScrollView hsvIngredients = findViewById(R.id.hsv_ingredients);

        // 데이터 받기
        Recipe recipe = (Recipe) getIntent().getSerializableExtra("recipe");

        if (recipe != null) {



            tvTitle.setText(recipe.getName());
            tvSteps.setText(formatSteps(recipe.getSteps()));

            // ✅ 시간·난이도·인분 표시
            if (recipe.getTime() != null) {
                timeInfo.setText(recipe.getTime() + "분");
            }
            if (recipe.getLevel() != null) {
                levelInfo.setText(recipe.getLevel());
            }
            if (recipe.getScore() != 0) {
                servingInfo.setText(recipe.getScore() + "점");
            }

            // 둥근 모서리 반경(dp → px 변환)
            int radiusInPx = (int) (30 * getResources().getDisplayMetrics().density);

            // 대표 이미지
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(radiusInPx))
                    .into(imgRecipe);

            // 책 영역 이미지도 동일하게
            Glide.with(this)
                    .load(recipe.getImageUrl())
                    .transform(new CenterCrop(), new RoundedCorners(radiusInPx))
                    .into(imgFood);

            // 재료 이미지
            if (recipe.getIngredients() != null) {
                int sizeInDp = 80; // 원하는 크기
                int sizeInPx = (int) (sizeInDp * getResources().getDisplayMetrics().density);
                int marginInDp = 8; // 음식 사이 간격
                int marginInPx = (int) (marginInDp * getResources().getDisplayMetrics().density);

                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    ImageView img = new ImageView(this);

                    // 마진 포함한 LayoutParams
                    LinearLayout.LayoutParams params =
                            new LinearLayout.LayoutParams(sizeInPx, sizeInPx);
                    params.setMargins(0, 0, marginInPx, 0); // 오른쪽 마진만 적용
                    img.setLayoutParams(params);

                    Glide.with(this)
                            .load(recipe.getIngredients().get(i).getImage())
                            .transform(new CenterCrop(), new RoundedCorners(radiusInPx))
                            .into(img);


                    img.setOnClickListener(v -> {
                        IngredientsBottomSheet sheet = IngredientsBottomSheet.newInstance(
                                "재료 " + recipe.getIngredients().size(),
                                recipe.getIngredients()
                        );
                        sheet.show(getSupportFragmentManager(), "IngredientsBottomSheet");
                    });

                    layoutIngredients.addView(img);
                }
            }

            // 책 구매 링크 버튼
            tvBookTitle.setText("책 구매하러 가기");
            layoutBook.setOnClickListener(v -> {
                if (recipe.getBook() != null && !recipe.getBook().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(recipe.getBook()));
                    startActivity(intent);
                }
            });
        }

        // 뒤로가기 버튼
        btnBack.setOnClickListener(v -> finish());

        // 공유 버튼
        btnShare.setOnClickListener(v -> {
            if (recipe != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, recipe.getName() + "\n" + recipe.getBook());
                startActivity(Intent.createChooser(shareIntent, "레시피 공유"));
            }
        });
    }

    private String formatSteps(List<String> steps) {
        StringBuilder sb = new StringBuilder();
        if (steps != null) {
            for (int i = 0; i < steps.size(); i++) {
                sb.append(i + 1).append(". ").append(steps.get(i)).append("\n\n");
            }
        }
        return sb.toString();
    }
}
