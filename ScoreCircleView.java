package com.example.it_contest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ScoreCircleView extends View {

    private Paint paintArc;
    private Paint paintTextScore;
    private Paint paintTextLabel; // "Total Score" 텍스트용 Paint
    private Paint paintIndicator; // 하단 인디케이터용 Paint
    private RectF rectF;

    private int morningScore = 0;
    private int lunchScore = 0;
    private int dinnerScore = 0;
    private int snackScore = 0;

    public ScoreCircleView(Context context) {
        super(context);
        init();
    }

    public ScoreCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScoreCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 원형 그래프 호(arc) 설정
        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(50); // 두께를 조금 더 두껍게
        paintArc.setStrokeCap(Paint.Cap.ROUND); // 호의 끝을 둥글게

        // 중앙 점수 텍스트 설정
        paintTextScore = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextScore.setColor(Color.BLACK);
        paintTextScore.setTextSize(140); // 점수 글씨 크기
        paintTextScore.setTextAlign(Paint.Align.CENTER);
        paintTextScore.setFakeBoldText(true);

        // "Total Score" 라벨 텍스트 설정
        paintTextLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextLabel.setColor(Color.GRAY);
        paintTextLabel.setTextSize(40); // 라벨 글씨 크기
        paintTextLabel.setTextAlign(Paint.Align.CENTER);

        // 하단 인디케이터(원 + 텍스트) 설정
        paintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintIndicator.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();
    }

    // onAttachedToWindow는 미리보기 등을 위해 그대로 둡니다.
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (morningScore == 0 && lunchScore == 0 && dinnerScore == 0 && snackScore == 0) {
            // 이미지와 유사한 점수 값으로 예시 설정
            setScores(85, 95, 75, 25);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // --- 1. 원형 그래프 그리기 ---
        float centerX = getWidth() / 2f;
        // 하단에 공간을 만들기 위해 원의 중심을 약간 위로 조정
        float centerY = getHeight() / 2f - 80;
        float radius = Math.min(getWidth(), getHeight()) / 3f;
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // 점수 및 색상 계산
        int total = (morningScore + lunchScore + dinnerScore + snackScore) / 4;
        int morningColor = getColorForScore(morningScore);
        int lunchColor = getColorForScore(lunchScore);
        int dinnerColor = getColorForScore(dinnerScore);
        int snackColor = getColorForScore(snackScore);

        // 각 섹션 사이에 간격을 주기 위한 설정
        float sweepAngle = 85f; // 각 호가 차지할 각도
        float gapAngle = 5f;    // 호 사이의 간격 각도
        float startAngle = -90f; // 12시 방향에서 시작

        // 순서: 아침(좌상), 점심(우상), 저녁(우하), 간식(좌하)
        // 이미지에서는 아침,점심,저녁,간식 순으로 시계방향으로 배치됩니다.
        paintArc.setColor(morningColor);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paintArc);
        startAngle += sweepAngle + gapAngle;

        paintArc.setColor(lunchColor);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paintArc);
        startAngle += sweepAngle + gapAngle;

        paintArc.setColor(dinnerColor);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paintArc);
        startAngle += sweepAngle + gapAngle;

        paintArc.setColor(snackColor);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paintArc);

        // --- 2. 중앙 텍스트 그리기 ---
        // 점수 텍스트
        Paint.FontMetrics scoreMetrics = paintTextScore.getFontMetrics();
        float scoreY = centerY - (scoreMetrics.ascent + scoreMetrics.descent) / 2;
        canvas.drawText(String.valueOf(total), centerX, scoreY, paintTextScore);

        // "Total Score" 라벨 텍스트
        float labelY = scoreY + 60; // 점수 바로 아래에 위치
        canvas.drawText("Total Score", centerX, labelY, paintTextLabel);

        // --- 3. 하단 인디케이터 그리기 ---
        float indicatorY = centerY + radius + 120; // 원 그래프 아래 위치
        float indicatorRadius = 15f;
        float indicatorTextY = indicatorY + indicatorRadius + 40;
        float sectionWidth = getWidth() / 4f;

        String[] labels = {"아침", "점심", "저녁", "간식"};
        int[] colors = {morningColor, lunchColor, dinnerColor, snackColor};

        for (int i = 0; i < 4; i++) {
            float indicatorX = sectionWidth * (i + 0.5f);

            // 인디케이터 색상 원
            paintIndicator.setColor(colors[i]);
            paintIndicator.setStyle(Paint.Style.FILL);
            canvas.drawCircle(indicatorX, indicatorY, indicatorRadius, paintIndicator);

            // 인디케이터 텍스트
            paintIndicator.setColor(Color.DKGRAY);
            paintIndicator.setTextSize(35);
            canvas.drawText(labels[i], indicatorX, indicatorTextY, paintIndicator);
        }
    }

    public void setScores(int morning, int lunch, int dinner, int snack) {
        this.morningScore = clamp(morning);
        this.lunchScore = clamp(lunch);
        this.dinnerScore = clamp(dinner);
        this.snackScore = clamp(snack);
        invalidate(); // View를 다시 그리도록 요청
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(score, 100));
    }

    // 색상 결정 로직은 기존 코드를 그대로 사용합니다.
    private int getColorForScore(int score) {
        if (score < 40) {
            return Color.parseColor("#E0E0E0"); // 회색 (이미지와 비슷하게 약간 수정)
        } else if (score < 70) {
            return Color.parseColor("#A5D6A7"); // 연한 초록 (이미지와 비슷하게 약간 수정)
        } else if (score < 90) {
            return Color.parseColor("#66BB6A"); // 중간 초록 (이미지와 비슷하게 추가)
        }
        else {
            return Color.parseColor("#2E7D32"); // 진한 초록 (이미지와 비슷하게 약간 수정)
        }
    }
}