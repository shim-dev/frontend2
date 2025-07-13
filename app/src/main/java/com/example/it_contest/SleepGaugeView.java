package com.example.it_contest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SleepGaugeView extends View {

    private float progress = 0.5f; // 0.0 ~ 1.0 (0%~100%)
    private Paint bgPaint, fgPaint;
    private OnSleepValueChangedListener listener;

    public SleepGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStrokeWidth(80f);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(Color.LTGRAY);

        fgPaint = new Paint();
        fgPaint.setAntiAlias(true);
        fgPaint.setStrokeWidth(80f);
        fgPaint.setStyle(Paint.Style.STROKE);
        fgPaint.setStrokeCap(Paint.Cap.ROUND);
        fgPaint.setColor(Color.parseColor("#96A98D")); // 게이지 색
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float stroke = getHeight() * 0.2f; // 높이의 절반! (진짜 통통하게)
        if (stroke > 120f) stroke = 120f;  // 너무 두껍게 보이면 적당히 제한

        bgPaint.setStrokeWidth(stroke);
        fgPaint.setStrokeWidth(stroke);

        float pad = stroke / 2f;
        float left = pad;
        float top = pad;
        float right = getWidth() - pad;
        float bottom = getHeight() * 0.85f; // 또는 0.8f 등으로 조절

        canvas.drawArc(left, top, right, bottom, 180f, 180f, false, bgPaint);
        canvas.drawArc(left, top, right, bottom, 180f, 180f * progress, false, fgPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);

        float x = event.getX();
        float y = event.getY();

        float stroke = getHeight() * 0.2f;
        if (stroke > 120f) stroke = 120f;
        float pad = stroke / 2f;
        float left = pad;
        float top = pad;
        float right = getWidth() - pad;
        float bottom = getHeight() * 0.85f;

        float centerX = (left + right) / 2f;
        float centerY = (top + bottom) / 2f;
        float radius = (right - left) / 2f;

        if (y > centerY) return false;

        float dx = x - centerX;
        float dy = y - centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float minR = radius - stroke / 2f;
        float maxR = radius + stroke / 2f;
        if (dist < minR || dist > maxR) return false;

        if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            // atan2의 결과 각도(라디안): 왼쪽 끝(π), 위쪽(π/2), 오른쪽 끝(0)
            double angleRad = Math.atan2(centerY - y, x - centerX);

            // angleDeg: 왼쪽 180, 위쪽 90, 오른쪽 0
            double angleDeg = Math.toDegrees(angleRad);

            // (angleDeg: 180 -> 0) 을 (progress: 0 -> 1)로 매핑
            // clamp: 180~0만 반영 (180 초과/0 미만은 끝으로 fix)
            double percent = (180 - angleDeg) / 180.0;
            if (percent < 0) percent = 0;
            if (percent > 1) percent = 1;

            progress = (float) percent;

            if (listener != null) {
                int hour = (int) (4 + progress * 8);
                listener.onValueChanged(hour);
            }
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }

    // 리스너 등록 (액티비티에서 시간 갱신용)
    public void setOnSleepValueChangedListener(OnSleepValueChangedListener l) {
        listener = l;
    }

    public interface OnSleepValueChangedListener {
        void onValueChanged(int hour);
    }

    public void setProgress(float p) {
        if (p < 0) p = 0;
        if (p > 1) p = 1;
        progress = p;
        invalidate();
        if (listener != null) {
            int hour = (int) (4 + progress * 8);
            listener.onValueChanged(hour);
        }
    }



}