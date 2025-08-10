package com.example.it_contest;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelLayerOptions;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;
import com.kakao.vectormap.label.LabelTextBuilder;
import com.kakao.vectormap.label.LabelTextStyle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MapActivity";

    private MapView mapView;
    private KakaoMap kakaoMap;

    private LabelLayer markerLayer;
    private final Set<String> markerKeys = new HashSet<>();

    private View placeCard;
    private TextView placeName, placeAddress, placePhone;

    // 검색 UI
    private EditText etSearch;
    private ImageView btnSearch, btnClear;

    // 기본 위치(서울 시청)
    private final LatLng defaultPos = LatLng.from(37.5665, 126.9780);

    // 카카오 로컬 REST 키
    private static final String KAKAO_REST_API_KEY = "KakaoAK 8daecf643c51d324e4bdd6947ec631ff";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        KakaoMapSdk.init(this, "2299312293945670bc7f44edc58786ae");
        setContentView(R.layout.map_find);

        // 뷰 바인딩
        mapView = findViewById(R.id.map_view);
        placeCard = findViewById(R.id.place_card);
        placeName = findViewById(R.id.place_name);
        placeAddress = findViewById(R.id.place_address);
        placePhone = findViewById(R.id.place_phone);
        etSearch = findViewById(R.id.et_search);
        btnSearch = findViewById(R.id.btn_search);
        btnClear = findViewById(R.id.btn_clear);

        // 검색 버튼
        btnSearch.setOnClickListener(v -> {
            String q = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
            if (!TextUtils.isEmpty(q)) performSearch(q);
        });

        // 키보드 검색 액션
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)) {
                String q = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
                if (!TextUtils.isEmpty(q)) performSearch(q);
                return true;
            }
            return false;
        });

        // X 버튼(초기화)
        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            hidePlaceCard();
            resetToDefault();  // 기본(비건/샐러드) 핀 복원
        });

        // 지도 시작
        mapView.start(new MapLifeCycleCallback() {
            @Override public void onMapDestroy() {}
            @Override public void onMapError(Exception error) {
                Log.e(LOG_TAG, "onMapError", error);
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap map) {
                kakaoMap = map;

                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(defaultPos, 15));
                initializeLayersAndListeners();

                // 최초 기본 핀(비건/샐러드)
                resetToDefault();
            }
        });
    }

    /** 기본 키워드 복원 */
    private void resetToDefault() {
        clearMarkers();
        searchNearbyPlaces(defaultPos.getLatitude(), defaultPos.getLongitude(), "비건", false, false);
        searchNearbyPlaces(defaultPos.getLatitude(), defaultPos.getLongitude(), "샐러드", false, false);
    }

    /** 검색 실행: 기존 핀 삭제 → 해당 키워드만 추가 → 첫 결과로 카메라 센터 */
    private void performSearch(String keyword) {
        hidePlaceCard();
        clearMarkers();
        // 중심좌표 미사용(첫 결과로 카메라 이동하므로 UX 동일)
        searchNearbyPlaces(defaultPos.getLatitude(), defaultPos.getLongitude(), keyword, false, true);
    }

    /** 레이어/리스너 초기화 */
    private void initializeLayersAndListeners() {
        if (kakaoMap == null) return;

        if (markerLayer == null) {
            markerLayer = kakaoMap.getLabelManager().addLayer(
                    LabelLayerOptions.from("marker_layer")
                            .setClickable(true)
                            .setZOrder(1000)
            );
        }

        kakaoMap.setOnMapClickListener((map, latLng, screenPoint, poi) -> hidePlaceCard());

        // boolean 반환형 리스너
        kakaoMap.setOnLabelClickListener((map, layer, label) -> {
            try {
                Object tag = label.getTag();
                if (tag instanceof JSONObject) {
                    showPlaceCard((JSONObject) tag);
                    return true; // 이벤트 소비
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "라벨 태그 파싱 오류", e);
            }
            return false;
        });
    }

    /** 모든 마커 제거 (레이어는 유지) */
    private void clearMarkers() {
        try {
            if (markerLayer != null) {
                markerLayer.removeAll(); // ← removeLayer 대신 이것만
            }
        } catch (Exception ignore) {}
        markerKeys.clear();
    }

    /** 키워드 검색
     * @param clearBefore  (미사용: 외부에서 clearMarkers 처리)
     * @param centerOnFirst 첫 결과로 카메라 이동 여부
     */
    private void searchNearbyPlaces(double latitude, double longitude, String keyword,
                                    boolean clearBefore, boolean centerOnFirst) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                String query = URLEncoder.encode(keyword, StandardCharsets.UTF_8.name());
                String url = "https://dapi.kakao.com/v2/local/search/keyword.json"
                        + "?y=" + latitude
                        + "&x=" + longitude
                        + "&radius=2000"
                        + "&category_group_code=FD6"
                        + "&query=" + query;

                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Authorization", KAKAO_REST_API_KEY);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) sb.append(line);
                    in.close();

                    runOnUiThread(() -> addMarkersToMap(sb.toString(), keyword, centerOnFirst));
                } else {
                    Log.e(LOG_TAG, "API 실패 code=" + conn.getResponseCode() + " (" + keyword + ")");
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "API 호출 예외 (" + keyword + ")", e);
            }
        });
    }

    /** 기존 호환용 (append & no center) */
    private void searchNearbyPlaces(double latitude, double longitude, String keyword) {
        searchNearbyPlaces(latitude, longitude, keyword, false, false);
    }

    /** 결과 → 마커 추가 (옵션: 첫 결과로 카메라 이동) */
    private void addMarkersToMap(String jsonResponse, String keyword, boolean centerOnFirst) {
        if (kakaoMap == null || markerLayer == null) return;

        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray docs = root.getJSONArray("documents");
            if (docs.length() == 0) return;

            LabelTextStyle textStyle = LabelTextStyle.from(14, Color.BLACK, 1, Color.WHITE);
            Bitmap pinBitmap = getBitmapFromVector(R.drawable.ic_map_pin);
            LabelStyle style = LabelStyle.from(pinBitmap)
                    .setAnchorPoint(0.5f, 1.0f)
                    .setTextStyles(textStyle);
            LabelStyles pinStyles = kakaoMap.getLabelManager().addLabelStyles(LabelStyles.from(style));

            LatLng firstPos = null;

            for (int i = 0; i < docs.length(); i++) {
                JSONObject doc = docs.getJSONObject(i);
                String name = doc.getString("place_name");
                String x = doc.getString("x");
                String y = doc.getString("y");

                String key = name + "|" + x + "|" + y;
                if (markerKeys.contains(key)) continue;
                markerKeys.add(key);

                LatLng pos = LatLng.from(Double.parseDouble(y), Double.parseDouble(x));
                if (firstPos == null) firstPos = pos;

                LabelTextBuilder textBuilder = new LabelTextBuilder().setTexts(name);
                LabelOptions options = LabelOptions.from(pos)
                        .setStyles(pinStyles)
                        .setTexts(textBuilder)
                        .setRank(2)
                        .setClickable(true)
                        .setTag(doc);

                markerLayer.addLabel(options);
            }

            if (centerOnFirst && firstPos != null) {
                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(firstPos, 16));
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "마커 추가 예외 (" + keyword + ")", e);
        }
    }

    private void showPlaceCard(JSONObject place) {
        try {
            String name = place.optString("place_name", "");
            String address = place.optString("road_address_name",
                    place.optString("address_name", "주소 정보 없음"));
            String phone = place.optString("phone", "전화번호 없음");

            placeName.setText(name);
            placeAddress.setText(address);
            placePhone.setText(phone);

            if (placeCard.getVisibility() != View.VISIBLE) {
                placeCard.setVisibility(View.VISIBLE);
                placeCard.post(() -> {
                    placeCard.setTranslationY(placeCard.getHeight());
                    placeCard.animate().translationY(0f).setDuration(180).start();
                });
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "showPlaceCard error", e);
        }
    }

    private void hidePlaceCard() {
        if (placeCard.getVisibility() == View.VISIBLE) {
            placeCard.animate()
                    .translationY(placeCard.getHeight())
                    .setDuration(150)
                    .withEndAction(() -> placeCard.setVisibility(View.GONE))
                    .start();
        }
    }

    private Bitmap getBitmapFromVector(@DrawableRes int drawableId) {
        Drawable d = ResourcesCompat.getDrawable(getResources(), drawableId, getTheme());
        if (d == null) return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

        int dp = 40; // 고정 사이즈 dp (히트박스 넉넉)
        float density = getResources().getDisplayMetrics().density;
        int w = (int) (dp * density);
        int h = (int) (dp * density);

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        d.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.resume();
    }

    @Override
    protected void onPause() {
        if (mapView != null) mapView.pause();
        super.onPause();
    }
}
