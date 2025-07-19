package com.example.it_contest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PointChargeActivity extends BaseActivity {

    private CardView[] cards;
    private TextView[] pointTextViews;
    private int selectedIndex = -1;

    private int[] pointValues = {500, 1000, 2000, 3000};
    private int[] paymentAmounts = {500, 900, 1600, 2500};

    private TextView chargePointText, paymentAmountText, feeText, totalAmountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.point_charge);

        setupBottomNavigation("challenge");

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
}
