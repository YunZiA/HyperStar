package com.yunzia.hyperstar.hook.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircleRingView extends View {
    private static final int RING_RADIUS = 200;
    private static final int CIRCLE_RADIUS = 30;
    private static final int CENTER_X = 400; // Adjust based on your layout
    private static final int CENTER_Y = 400; // Adjust based on your layout
    private static final int CIRCLE_SPACING = (360 - 360 / 8 * 2) / 8; // Space between circles

    private Paint ringPaint;
    private Paint circlePaint;
    private Paint textPaint;
    private float[] circleAngles = new float[8];
    private int selectedIndex = -1;

    public CircleRingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleRingView(Context context) {
        super(context);
        init();
    }

    private void init() {
        ringPaint = new Paint();
        ringPaint.setColor(Color.GRAY);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(10);

        circlePaint = new Paint();
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);

        for (int i = 0; i < 8; i++) {
            circleAngles[i] = (float) (Math.toRadians(360 / 8 * i + CIRCLE_SPACING / 2));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw ring
        canvas.drawCircle(CENTER_X, CENTER_Y, RING_RADIUS, ringPaint);

        // Draw circles
        for (int i = 0; i < 8; i++) {
            float x = CENTER_X + (RING_RADIUS - CIRCLE_RADIUS) * (float) Math.cos(circleAngles[i]);
            float y = CENTER_Y + (RING_RADIUS - CIRCLE_RADIUS) * (float) Math.sin(circleAngles[i]);
            canvas.drawCircle(x, y, CIRCLE_RADIUS, circlePaint);

            // Optionally draw text or other indicators
            String text = String.valueOf(i + 1);
            float textWidth = textPaint.measureText(text);
            canvas.drawText(text, x - textWidth / 2, y - CIRCLE_RADIUS / 2, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        float distanceToCenter = (float) Math.sqrt(Math.pow(touchX - CENTER_X, 2) + Math.pow(touchY - CENTER_Y, 2));

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (distanceToCenter >= RING_RADIUS - CIRCLE_RADIUS && distanceToCenter <= RING_RADIUS + CIRCLE_RADIUS) {
                for (int i = 0; i < 8; i++) {
                    float x = CENTER_X + (RING_RADIUS - CIRCLE_RADIUS) * (float) Math.cos(circleAngles[i]);
                    float y = CENTER_Y + (RING_RADIUS - CIRCLE_RADIUS) * (float) Math.sin(circleAngles[i]);

                    if (isPointInsideCircle(touchX, touchY, x, y, CIRCLE_RADIUS)) {
                        selectedIndex = i;
                        invalidate(); // Request re-draw
                        performAction(i);
                        return true;
                    }
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            selectedIndex = -1;
            invalidate(); // Request re-draw
        }

        return super.onTouchEvent(event);
    }

    private boolean isPointInsideCircle(float px, float py, float cx, float cy, float radius) {
        return (px - cx) * (px - cx) + (py - cy) * (py - cy) <= (radius * radius);
    }

    private void performAction(int index) {
        // Handle the action for the selected circle
        // For example, start a new activity, show a toast, etc.
        // Here, we'll just print the index to the log
        System.out.println("Circle " + (index + 1) + " selected");
    }
}