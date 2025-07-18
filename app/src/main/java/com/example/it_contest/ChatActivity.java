package com.example.it_contest;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ContextThemeWrapper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import android.widget.Button;

public class ChatActivity extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private AppCompatButton buttonMeal;
    private EditText editTextMessage;
    private ImageButton buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 상태바 밝은 아이콘
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // ✅ 필수 뷰 초기화
        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);

        // 전송 버튼 이벤트
        buttonSend.setOnClickListener(v -> {
            String userMessage = editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addAnswerBubble(userMessage);  // 사용자 말풍선 추가
                editTextMessage.setText("");

                if (userMessage.contains("기록")) {
                    addRecordOptions();  // ✅ "기록" 포함 시 추가 뷰 출력
                }
            }
        });
        addRecordOptions();
    }
    private void addWaterTracker() {
        View waterView = getLayoutInflater().inflate(R.layout.water_tracker, chatContainer, false);
        GridLayout waterGrid = waterView.findViewById(R.id.waterGrid);

        for (int i = 0; i < 8; i++) {
            ImageView cup = new ImageView(this);
            cup.setImageResource(R.drawable.ic_cup_off);  // 선택 전 이미지
            cup.setPadding(12, 12, 12, 12);

            final int index = i;
            cup.setOnClickListener(v -> {
                boolean isSelected = v.getTag() != null && (boolean) v.getTag();
                if (isSelected) {
                    cup.setImageResource(R.drawable.ic_cup_off);
                    v.setTag(false);
                } else {
                    cup.setImageResource(R.drawable.ic_cup_on);  // 선택 시 이미지
                    v.setTag(true);
                }
            });

            cup.setTag(false);  // 기본 상태
            waterGrid.addView(cup);
        }

        Button btnComplete = waterView.findViewById(R.id.btnCompleteWater);
        btnComplete.setOnClickListener(v -> {
            int count = 0;
            for (int i = 0; i < waterGrid.getChildCount(); i++) {
                View child = waterGrid.getChildAt(i);
                if (child.getTag() != null && (boolean) child.getTag()) {
                    count++;
                }
            }
            Toast.makeText(this, "오늘 마신 물: " + count + "잔", Toast.LENGTH_SHORT).show();
            addBotMessage("기록이 완료되었습니다.");
        });

        chatContainer.addView(waterView);
        scrollToBottom();
    }

    private void addSleepTracker() {
        View sleepView = getLayoutInflater().inflate(R.layout.sleep_tracker, chatContainer, false);

        EditText editHour = sleepView.findViewById(R.id.editHour);
        EditText editMinute = sleepView.findViewById(R.id.editMinute);
        Button btnCompleteSleep = sleepView.findViewById(R.id.btnCompleteSleep);

        btnCompleteSleep.setOnClickListener(v -> {
            String hourStr = editHour.getText().toString().trim();
            String minuteStr = editMinute.getText().toString().trim();

            // 유효성 검사: 빈 값이면 0으로 처리
            int hour = hourStr.isEmpty() ? 0 : Integer.parseInt(hourStr);
            int minute = minuteStr.isEmpty() ? 0 : Integer.parseInt(minuteStr);

            // 선택한 수면 시간 출력
            addAnswerBubble(hour + "시간 " + minute + "분 수면 기록");
            addBotMessage("기록이 완료되었습니다.");
        });

        chatContainer.addView(sleepView);
        scrollToBottom();
    }

    private void addBotMessage(String message) {
        LinearLayout messageLayout = createMessageLayout();

        ImageView botImage = createBotImage();
        messageLayout.addView(botImage);

        TextView textView = createBotText(message);
        messageLayout.addView(textView);

        chatContainer.addView(messageLayout);
        scrollToBottom();
    }

    private void addMealOptions() {
        LinearLayout bubbleLayout = createMessageLayout();
        bubbleLayout.setOrientation(LinearLayout.VERTICAL);

        // 아이콘 + 질문 텍스트
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        ImageView botImage = createBotImage();
        row.addView(botImage);

        TextView textView = createBotText("어느 식단을 기록할까?");
        row.addView(textView);
        bubbleLayout.addView(row);

        // 버튼들
        String[] options = {"🔍 아침", "🌞 점심", "🌙 저녁", "🍪 간식"};
        for (String option : options) {
            AppCompatButton button = createStyledOptionButton(option); // ✅ 여기서 공통 스타일 메서드 사용
            button.setOnClickListener(v -> {
                Toast.makeText(this, option + " 선택됨", Toast.LENGTH_SHORT).show();
                addAnswerBubble(option);
                addBotPromptForImage(option);// 사용자 선택을 대화창에 출력
            });

            bubbleLayout.addView(button);
        }

        chatContainer.addView(bubbleLayout);
        scrollToBottom();
    }

    private AppCompatButton createStyledOptionButton(String text) {
        AppCompatButton button = new AppCompatButton(this);
        button.setText(text);
        button.setTextSize(16f);
        button.setTypeface(null, Typeface.BOLD);
        button.setTextColor(Color.BLACK);
        button.setBackgroundResource(R.drawable.outline_button); // 공통 테두리
        button.setAllCaps(false); // 대문자 방지
        button.setPadding(32, 24, 32, 24);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 12, 0, 0);
        button.setLayoutParams(params);

        return button;
    }
    private void addAnswerBubble(String text) {
        // wrapper (말풍선 정렬용)
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.HORIZONTAL);
        wrapper.setGravity(Gravity.END);  // 오른쪽 정렬

        LinearLayout.LayoutParams wrapperParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        wrapperParams.setMargins(12, 12, 12, 18);  // 마지막 24가 아래쪽 여백
        wrapper.setLayoutParams(wrapperParams);

        // 말풍선 전체 (가로 전체 너비)
        LinearLayout bubbleLayout = new LinearLayout(this);
        bubbleLayout.setOrientation(LinearLayout.HORIZONTAL);
        bubbleLayout.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        bubbleLayout.setPadding(32, 28, 32, 28);
        bubbleLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.answer_bubble));
        bubbleLayout.setElevation(3f);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bubbleLayout.setLayoutParams(bubbleParams);

        // 텍스트
        TextView answer = new TextView(this);
        answer.setText(text);
        answer.setTextSize(16f);
        answer.setTypeface(null, Typeface.BOLD);
        answer.setTextColor(Color.BLACK);
        answer.setGravity(Gravity.END);
        answer.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // 나머지 공간 모두 차지
        );
        textParams.setMargins(0, 0, 48, 0);
        answer.setLayoutParams(textParams);

        ImageView userIcon = new ImageView(new ContextThemeWrapper(this, R.style.UserIconStyle), null, 0);
        userIcon.setImageResource(R.drawable.user_image);

        // 말풍선 안에 텍스트 + 아이콘 (오른쪽 정렬)
        bubbleLayout.addView(answer);
        bubbleLayout.addView(userIcon);

        // wrapper에 bubble 추가
        wrapper.addView(bubbleLayout);
        chatContainer.addView(wrapper);
        scrollToBottom();
    }

    private LinearLayout createMessageLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(16, 16, 16, 16);
        layout.setBackground(ContextCompat.getDrawable(this, R.drawable.chat_bubble));
        layout.setElevation(6f);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(12, 12, 12, 0);
        layout.setLayoutParams(layoutParams);

        return layout;
    }
    private ImageView createBotImage() {
        ImageView image = new ImageView(this);
        int size = getResources().getDimensionPixelSize(R.dimen.bot_icon_size);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
        lp.setMarginStart(getResources().getDimensionPixelSize(R.dimen.bot_icon_margin));
        image.setLayoutParams(lp);
        image.setImageResource(R.drawable.bot_image);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP); // 스타일과 동일
        return image;
    }
    private TextView createBotText(String msg) {
        TextView text = new TextView(this);
        text.setText(msg);
        text.setTextSize(16f);
        text.setTypeface(null, Typeface.BOLD);
        text.setTextColor(Color.BLACK);
        text.setPadding(16, 0, 0, 0);

        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT  // 높이를 말풍선 높이에 맞추기 위해
        ));

        return text;
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void addBotPromptForImage(String mealText) {
        String prompt = mealText + "의 사진을 기록해주세요!";
        addBotMessage(prompt);  // 기존 봇 메시지 함수 재활용
    }

    private void addRecordOptions() {
        // bot_record_option.xml 레이아웃을 인플레이트
        View recordView = getLayoutInflater().inflate(R.layout.bot_record_option, chatContainer, false);

        // 버튼들 찾기
        AppCompatButton buttonMeal = recordView.findViewById(R.id.buttonMeal);
        AppCompatButton buttonWater = recordView.findViewById(R.id.buttonWater);
        AppCompatButton buttonSleep = recordView.findViewById(R.id.buttonSleep);
        AppCompatButton buttonNews = recordView.findViewById(R.id.buttonNews);

        // 이벤트 등록
        buttonMeal.setOnClickListener(v -> {
            addAnswerBubble("🍽 식단 기록");
            addMealOptions();  // 기존 함수 재사용
        });

        buttonWater.setOnClickListener(v -> {
            addAnswerBubble("💧 물 기록");
            addBotMessage("오늘 마신 물의 양을 입력해주세요!");
            addWaterTracker();
        });

        buttonSleep.setOnClickListener(v -> {
            addAnswerBubble("🌙 수면 기록");
            addBotMessage("어제 수면 시간을 입력해주세요!");
            addSleepTracker();
        });

        buttonNews.setOnClickListener(v -> {
            addAnswerBubble("📰 저속노화 소식");
            addBotMessage("최신 건강 뉴스로 이동할게요!");
        });

        // 전체 View를 대화에 추가
        chatContainer.addView(recordView);
        scrollToBottom();
    }

    private AppCompatButton createRecordButton(String text, View.OnClickListener listener) {
        AppCompatButton button = new AppCompatButton(this);
        button.setText(text);
        button.setTextSize(16f);
        button.setTypeface(null, Typeface.BOLD);
        button.setTextColor(Color.BLACK);
        button.setBackgroundResource(R.drawable.outline_button);
        button.setAllCaps(false);
        button.setPadding(32, 24, 32, 24);
        button.setOnClickListener(listener);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 12, 0, 0);
        button.setLayoutParams(params);

        return button;
    }
}


