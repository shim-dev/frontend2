package com.example.it_contest;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.it_contest.network.ApiService;
import com.example.it_contest.network.ChatMealResponse;
import com.example.it_contest.network.DietRecord;
import com.example.it_contest.network.FoodSearchResult;
import com.example.it_contest.network.RetrofitClient;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakfastBottomSheet extends BottomSheetDialogFragment {

    public static final String RESULT_KEY_SAVED   = "bb_saved";
    public static final String RESULT_KEY_DELETED = "bb_deleted";

    // UI
    private EditText    foodInput;
    private TextView    emptyStateText;
    private LinearLayout selectedListContainer;
    private TextView    dateText;

    // args
    private String nickname;
    private String mealType = "아침";
    private String date; // yyyy-MM-dd

    // 검색 결과 화면으로 이동/복귀
    private ActivityResultLauncher<Intent> searchResultLauncher;

    // 이미지 관련 런처/상태
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Uri>     takePictureLauncher;
    private ActivityResultLauncher<String>  pickImageLauncher;
    private Uri tempPhotoUri;

    /** 편한 생성자 */
    public static BreakfastBottomSheet newInstance(String date, String nickname, String mealType) {
        BreakfastBottomSheet f = new BreakfastBottomSheet();
        Bundle b = new Bundle();
        b.putString("date", date);
        b.putString("nickname", nickname);
        b.putString("meal_type", mealType);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 갤러리 선택
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) analyzeImageAndShowResults(uri);
                });

        // 사진 촬영
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                ok -> {
                    if (ok && tempPhotoUri != null) analyzeImageAndShowResults(tempPhotoUri);
                });

        // 카메라 권한
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) launchCamera();
                    else Toast.makeText(getContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.breakfast_bottom_sheet, container, false);

        // UI refs
        foodInput             = view.findViewById(R.id.foodInput);
        emptyStateText        = view.findViewById(R.id.emptyStateText);
        selectedListContainer = view.findViewById(R.id.selectedListContainer);
        dateText              = view.findViewById(R.id.dateText);

        // args
        Bundle args = getArguments();
        if (args != null) {
            date     = args.getString("date");
            nickname = args.getString("nickname");
            mealType = args.getString("meal_type", "아침");
        }

        // 날짜 표시 (예: "08\n13")
        if (!TextUtils.isEmpty(date) && date.length() >= 10) {
            String mm = date.substring(5, 7);
            String dd = date.substring(8, 10);
            dateText.setText(String.format(Locale.getDefault(), "%s\n%s", mm, dd));
        }

        // 검색 화면에서 선택된 음식 받아오기 → 저장 API 호출
        searchResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        FoodSearchResult selectedFood =
                                result.getData().getParcelableExtra("selected_food");
                        if (selectedFood != null) addMealOnServer(selectedFood);
                    }
                });

        // 🔍 검색 버튼
        view.findViewById(R.id.searchButton).setOnClickListener(v -> {
            String query = foodInput.getText().toString().trim();
            if (TextUtils.isEmpty(query)) {
                Toast.makeText(getContext(), "음식 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            searchFoodOnServer(query);
        });

        // 📷 카메라/갤러리 버튼
        view.findViewById(R.id.cameraButton).setOnClickListener(v -> showImagePickDialog());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 보톰시트 열릴 때마다 해당 날짜/끼니 전체 불러오기
        fetchAndRenderAll();
    }

    // -------------------- 서버 연동 --------------------

    /** Gemini 텍스트 검색 → 후보 리스트 화면으로 이동 */
    private void searchFoodOnServer(String query) {
        ApiService api = RetrofitClient.getApiService();
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("query", query);

        api.searchFood(body).enqueue(new Callback<List<FoodSearchResult>>() {
            @Override public void onResponse(Call<List<FoodSearchResult>> call, Response<List<FoodSearchResult>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    Intent intent = new Intent(getActivity(), SearchResultMain.class);
                    intent.putExtra("query", query);
                    intent.putParcelableArrayListExtra("food_results", new ArrayList<>(res.body()));
                    searchResultLauncher.launch(intent);
                } else {
                    Toast.makeText(getContext(), "검색 실패: " + res.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<FoodSearchResult>> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 후보에서 +를 눌렀을 때 서버에 저장 */
    private void addMealOnServer(FoodSearchResult food) {
        addMealOnServer(food.getName(), food.getScore(), food.getNote());
    }

    /** 이름/점수로 직접 저장 (이미지 분석 결과를 그대로 저장할 때 사용) */
    private void addMealOnServer(String name, int score, String note) {
        ApiService api = RetrofitClient.getApiService();

        JsonObject body = new JsonObject();
        body.addProperty("nickname", nickname);
        body.addProperty("meal_type", mealType);
        body.addProperty("date", date);

        JsonObject fd = new JsonObject();
        fd.addProperty("name",  name);
        fd.addProperty("score", score);
        fd.addProperty("note",  note);
        body.add("food_details", fd);

        api.addMealRecord(body).enqueue(new Callback<DietRecord>() {
            @Override public void onResponse(Call<DietRecord> call, Response<DietRecord> res) {
                if (res.isSuccessful()) {
                    fetchAndRenderAll(); // 저장 성공 → 목록 재로딩
                    Bundle b = new Bundle();
                    b.putString("record_id", res.body() != null ? res.body().getId() : "");
                    getParentFragmentManager().setFragmentResult(RESULT_KEY_SAVED, b);
                    foodInput.setText("");
                } else {
                    Toast.makeText(getContext(), "저장 실패: " + res.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<DietRecord> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** 날짜/끼니로 저장된 전체 레코드 조회 */
    private void fetchAndRenderAll() {
        ApiService api = RetrofitClient.getApiService();
        api.getMealsByDay(nickname, mealType, date).enqueue(new Callback<List<DietRecord>>() {
            @Override public void onResponse(Call<List<DietRecord>> call, Response<List<DietRecord>> res) {
                if (res.isSuccessful() && res.body() != null) {
                    renderRecords(res.body());
                } else {
                    Toast.makeText(getContext(), "불러오기 실패: " + res.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<List<DietRecord>> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // -------------------- 이미지 선택/촬영 --------------------

    private void showImagePickDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("이미지로 기록하기")
                .setItems(new CharSequence[]{"사진 촬영", "갤러리에서 불러오기"}, (d, which) -> {
                    if (which == 0) tryAskCameraAndLaunch();
                    else pickImageLauncher.launch("image/*");
                })
                .show();
    }

    private void tryAskCameraAndLaunch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        } else {
            launchCamera();
        }
    }

    private void launchCamera() {
        tempPhotoUri = createTempImageUri();
        if (tempPhotoUri == null) {
            Toast.makeText(getContext(), "임시 파일 생성 실패", Toast.LENGTH_SHORT).show();
            return;
        }
        takePictureLauncher.launch(tempPhotoUri);
    }

    @Nullable
    private Uri createTempImageUri() {
        try {
            File dir = new File(requireContext().getCacheDir(), "images");
            if (!dir.exists()) dir.mkdirs();
            File f = File.createTempFile("capture_", ".jpg", dir);
            return FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    f
            );
        } catch (Exception e) {
            return null;
        }
    }

    /** 이미지 → base64 → 서버 분석 → 검색결과 화면으로 띄우기(자동저장 X) */
    private void analyzeImageAndShowResults(@NonNull Uri uri) {
        try {
            String base64 = encodeToBase64(uri);
            if (base64 == null || base64.length() == 0) {
                Toast.makeText(getContext(), "이미지 인코딩 실패", Toast.LENGTH_SHORT).show();
                return;
            }
            String mime = requireContext().getContentResolver().getType(uri);
            if (mime == null) mime = "image/jpeg";

            ApiService api = RetrofitClient.getApiService();
            JsonObject body = new JsonObject();
            body.addProperty("nickname", nickname);
            body.addProperty("meal_type", mealType);
            body.addProperty("image_base64", base64);
            body.addProperty("image_mime", mime);

            api.chatMeal(body).enqueue(new Callback<ChatMealResponse>() {
                @Override public void onResponse(Call<ChatMealResponse> call, Response<ChatMealResponse> res) {
                    if (!res.isSuccessful() || res.body() == null) {
                        Toast.makeText(getContext(), "이미지 분석 실패: " + res.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<FoodSearchResult> results = new ArrayList<>();
                    ChatMealResponse resp = res.body();

                    if (resp.getMind() != null && resp.getMind().getItems() != null) {
                        for (ChatMealResponse.MindItem it : resp.getMind().getItems()) {
                            String name  = it.getFood();
                            int    score = it.getScore();
                            String note  = it.getNote();
                            String emoji = "🥗";
                            if (!TextUtils.isEmpty(name)) {
                                results.add(new FoodSearchResult(name, score, note, emoji));
                            }
                        }
                    }

                    if (results.isEmpty() && resp.getFoods() != null) {
                        for (String name : resp.getFoods()) {
                            results.add(new FoodSearchResult(name, 50, "이미지 인식 결과", "🍽️"));
                        }
                    }

                    if (results.isEmpty()) {
                        Toast.makeText(getContext(), "인식된 음식이 없어요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(getActivity(), SearchResultMain.class);
                    intent.putExtra("query", "사진 인식 결과");
                    intent.putParcelableArrayListExtra("food_results", results);
                    searchResultLauncher.launch(intent);
                }

                @Override public void onFailure(Call<ChatMealResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "이미지 분석 네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "이미지 처리 실패", Toast.LENGTH_SHORT).show();
        }
    }

    // -------------------- 렌더 & 삭제 --------------------

    /** 받아온 기록을 LinearLayout에 동적 추가 */
    private void renderRecords(List<DietRecord> list) {
        selectedListContainer.removeAllViews();

        if (list == null || list.isEmpty()) {
            selectedListContainer.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            return;
        }

        emptyStateText.setVisibility(View.GONE);
        selectedListContainer.setVisibility(View.VISIBLE);

        for (DietRecord r : list) {
            View item = buildItemView(r);
            selectedListContainer.addView(item);
        }
    }

    /** 텍스트 + X 버튼이 있는 간단한 줄을 코드로 생성 */
    private View buildItemView(DietRecord record) {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.drawable.rounded_white_shadow);
        int pad = dp(8);
        row.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(8);
        row.setLayoutParams(lp);

        TextView tv = new TextView(getContext());
        String title = record.getMessage();
        if (TextUtils.isEmpty(title) && record.getFoods() != null && !record.getFoods().isEmpty()) {
            title = record.getFoods().get(0);
        }
        tv.setText(title != null ? title : "");
        tv.setTextColor(0xFF000000);
        tv.setTextSize(16f);
        tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView btn = new TextView(getContext());
        btn.setText("✖");
        btn.setTextSize(18f);
        btn.setTextColor(0xFFFF0000);
        btn.setPadding(dp(12), dp(4), dp(12), dp(4));
        btn.setOnClickListener(v -> deleteRecord(record.getId()));

        row.addView(tv);
        row.addView(btn);
        return row;
    }

    private void deleteRecord(String recordId) {
        ApiService api = RetrofitClient.getApiService();
        api.deleteMealRecord(recordId).enqueue(new Callback<JsonObject>() {
            @Override public void onResponse(Call<JsonObject> call, Response<JsonObject> res) {
                if (res.isSuccessful()) {
                    fetchAndRenderAll();
                    Bundle b = new Bundle();
                    b.putString("record_id", recordId);
                    getParentFragmentManager().setFragmentResult(RESULT_KEY_DELETED, b);
                } else {
                    Toast.makeText(getContext(), "삭제 실패: " + res.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "네트워크 오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int dp(int v) {
        return (int) (getResources().getDisplayMetrics().density * v);
    }

    /** content Uri → Base64(헤더 없는 순수 문자열) */
    private @Nullable String encodeToBase64(@NonNull Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            if (is == null) return null;
            byte[] buf = new byte[8 * 1024];
            int n;
            while ((n = is.read(buf)) >= 0) {
                bos.write(buf, 0, n);
            }
            byte[] bytes = bos.toByteArray();
            // 개행 없이 인코딩해야 서버가 깔끔히 받습니다.
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

