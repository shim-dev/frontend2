package com.example.it_contest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * ScoreCircleView
 * - 아침/점심/저녁/간식 점수를 색으로 표시하는 4분할 링
 * - 중앙 숫자는 총점 표시 (기본: 4개 평균, 필요 시 setTotalOverride로 오버라이드)
 */
public class ScoreCircleView extends View {

    // Paints
    private Paint paintArc;
    private Paint paintTextScore;
    private Paint paintTextLabel;
    private Paint paintIndicator;

    private RectF rectF;

    // Scores (0~100)
    private int morningScore = 0;
    private int lunchScore = 0;
    private int dinnerScore = 0;
    private int snackScore = 0;

    // 중앙 Total 점수 오버라이드 (null이면 4개 평균 사용)
    private Integer totalOverride = null;

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
        // 링 아크
        paintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintArc.setStyle(Paint.Style.STROKE);
        paintArc.setStrokeWidth(50f);
        paintArc.setStrokeCap(Paint.Cap.ROUND);

        // 중앙 점수
        paintTextScore = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextScore.setColor(Color.BLACK);
        paintTextScore.setTextSize(140f);
        paintTextScore.setTextAlign(Paint.Align.CENTER);
        paintTextScore.setFakeBoldText(true);

        // "Total Score" 라벨
        paintTextLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTextLabel.setColor(Color.GRAY);
        paintTextLabel.setTextSize(40f);
        paintTextLabel.setTextAlign(Paint.Align.CENTER);

        // 하단 인디케이터
        paintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintIndicator.setTextAlign(Paint.Align.CENTER);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 1) 원형 그래프 배치 계산
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f - 80f; // 아래쪽 설명공간 확보를 위해 위로 올림
        float radius = Math.min(getWidth(), getHeight()) / 3f;
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        // 2) 점수와 색상
        int morningColor = getColorForScore(morningScore);
        int lunchColor = getColorForScore(lunchScore);
        int dinnerColor = getColorForScore(dinnerScore);
        int snackColor = getColorForScore(snackScore);

        // 3) 4분할 링 그리기
        float sweepAngle = 85f; // 각 섹션 각도
        float gapAngle = 5f;  // 섹션 사이 간격
        float startAngle = -90f; // 12시 시작

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

        // 4) 중앙 텍스트 (총점)
        int defaultTotal = (morningScore + lunchScore + dinnerScore + snackScore) / 4;
        int total = (totalOverride != null) ? clamp(totalOverride) : defaultTotal;

        Paint.FontMetrics fm = paintTextScore.getFontMetrics();
        float textY = centerY - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(String.valueOf(total), centerX, textY, paintTextScore);

        float labelY = textY + 60f;
        canvas.drawText("Total Score", centerX, labelY, paintTextLabel);

        // 5) 하단 인디케이터
        float indicatorY = centerY + radius + 120f;
        float indicatorRadius = 15f;
        float indicatorTextY = indicatorY + indicatorRadius + 40f;
        float sectionWidth = getWidth() / 4f;

        String[] labels = {"아침", "점심", "저녁", "간식"};
        int[] colors = {morningColor, lunchColor, dinnerColor, snackColor};

        for (int i = 0; i < 4; i++) {
            float x = sectionWidth * (i + 0.5f);

            paintIndicator.setStyle(Paint.Style.FILL);
            paintIndicator.setColor(colors[i]);
            canvas.drawCircle(x, indicatorY, indicatorRadius, paintIndicator);

            paintIndicator.setColor(Color.DKGRAY);
            paintIndicator.setTextSize(35f);
            canvas.drawText(labels[i], x, indicatorTextY, paintIndicator);
        }
    }

    /**
     * 끼니별 점수 설정 (0~100)
     */
    public void setScores(int morning, int lunch, int dinner, int snack) {
        this.morningScore = clamp(morning);
        this.lunchScore = clamp(lunch);
        this.dinnerScore = clamp(dinner);
        this.snackScore = clamp(snack);
        invalidate();
    }

    /**
     * 중앙 Total 점수를 지정(존재 끼니 평균 등). null 주면 4개 평균으로 복귀
     */
    public void setTotalOverride(Integer total) {
        this.totalOverride = total; // null 허용
        invalidate();
    }

    /**
     * 모두 0으로 초기화하고 Total 오버라이드 해제
     */
    public void clearScores() {
        this.morningScore = this.lunchScore = this.dinnerScore = this.snackScore = 0;
        this.totalOverride = null;
        invalidate();
    }

    private int clamp(int score) {
        return Math.max(0, Math.min(score, 100));
    }

    // 점수별 색상 매핑
    private int getColorForScore(int score) {
        if (score <= 10) {
            return Color.parseColor("#E0E0E0"); // 회색 (아주 낮음)
        } else if (score <= 20) {
            return Color.parseColor("#C8E6C9"); // 아주 연한 초록
        } else if (score <= 25) {
            return Color.parseColor("#A5D6A7"); // 연한 초록
        } else if (score <= 40) {
            return Color.parseColor("#81C784"); // 조금 연해진 중간 초록
        } else if (score <= 60) {
            return Color.parseColor("#66BB6A"); // 중간 초록
        } else if (score <= 80) {
            return Color.parseColor("#4CAF50"); // 진해진 중간 초록
        } else if (score <= 90) {
            return Color.parseColor("#388E3C"); // 진한 초록
        } else {
            return Color.parseColor("#2E7D32"); // 아주 진한 초록 (거의 만점)
        }
    }
}
