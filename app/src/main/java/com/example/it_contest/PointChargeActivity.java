package com.example.it_contest;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootItem;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class PointChargeActivity extends BaseActivity {

    private CardView[] cards;
    private TextView[] pointTextViews;
    private int selectedIndex = -1;

    private int[] pointValues = {500, 1000, 2000, 3000};
    private int[] paymentAmounts = {500, 900, 1600, 2500};

    private TextView chargePointText, paymentAmountText, feeText, totalAmountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String nickname = prefs.getString("nickname", null);

        if (nickname == null) {
            Toast.makeText(this, "로그인 정보가 없습니다. 다시 로그인해주세요.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_charge);

        setupBottomNavigation("my");

        cards = new CardView[]{
                findViewById(R.id.card_500),
                findViewById(R.id.card_1000),
                findViewById(R.id.card_2000),
                findViewById(R.id.card_3000)
        };

        pointTextViews = new TextView[]{
                findViewById(R.id.txt_500),
                findViewById(R.id.txt_1000),
                findViewById(R.id.txt_2000),
                findViewById(R.id.txt_3000)
        };

        chargePointText = findViewById(R.id.charge_point);
        paymentAmountText = findViewById(R.id.payment_amount);
        feeText = findViewById(R.id.payment_fee);
        totalAmountText = findViewById(R.id.payment_total);

        for (int i = 0; i < cards.length; i++) {
            int index = i;
            cards[i].setOnClickListener(v -> handleSelection(index));
        }

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // ✅ 결제 버튼 연결
        findViewById(R.id.confirm_button).setOnClickListener(v -> PaymentTest(v));
    }

    private void handleSelection(int index) {
        selectedIndex = index;

        for (int i = 0; i < cards.length; i++) {
            if (i == index) {
                cards[i].setCardBackgroundColor(Color.parseColor("#EEEEEE")); // 선택된 카드 색상
                pointTextViews[i].setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                cards[i].setCardBackgroundColor(Color.WHITE);
                pointTextViews[i].setTypeface(null, android.graphics.Typeface.NORMAL);
            }
        }

        int points = pointValues[index];
        int amount = paymentAmounts[index];
        int fee = (int) Math.round(amount * 0.01);
        int total = amount + fee;

        chargePointText.setText(points + "P");
        paymentAmountText.setText(amount + "원");
        feeText.setText(fee + "원");
        totalAmountText.setText(total + "원");
    }

    public void PaymentTest(View v) {
        BootUser user = new BootUser().setPhone("010-1234-5678"); // 구매자 정보

        BootExtra extra = new BootExtra()
                .setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)

        int amount = paymentAmounts[selectedIndex];  // 실제 결제 금액
        int point = pointValues[selectedIndex];
        int fee = (int) Math.round(amount * 0.01);  // 수수료
        int total = amount + fee;                   // 실제 결제할 금액

        List<BootItem> items = new ArrayList<>();
        BootItem item = new BootItem()
                .setName(point + "P 충전")               // 예: "1000P 충전"
                .setId("POINT_CHARGE_" + point)         // 예: "POINT_CHARGE_1000"
                .setQty(1)
                .setPrice((double) total);             // 예: 900.0
        items.add(item);

        Payload payload = new Payload();
        payload.setApplicationId("5b8f6a4d396fa665fdc2b5e8")
                .setOrderName(point + "P 포인트 충전")   // 예: "1000P 포인트 충전"
                .setOrderId("order_" + System.currentTimeMillis())  // 고유 주문 ID
                .setPrice((double) total)              // 실제 결제 금액
                .setUser(user)
                .setExtra(extra)
                .setItems(items);

        Map<String, Object> map = new HashMap<>();
        map.put("1", "abcdef");
        map.put("2", "abcdef55");
        map.put("3", 1234);
        payload.setMetadata(map);
//        payload.setMetadata(new Gson().toJson(map));

        Bootpay.init(getSupportFragmentManager())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("bootpay", "cancel: " + data);
                    }

                    @Override
                    public void onError(String data) {
                        Log.d("bootpay", "error: " + data);
                    }

                    @Override
                    public void onClose() {
                        Bootpay.removePaymentWindow();
                    }

                    @Override
                    public void onIssued(String data) {
                        Log.d("bootpay", "issued: " +data);
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
//                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                        return true; //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                        return false; //결제를 진행하지 않을때 false
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                    }
                }).requestPayment();
    }
}