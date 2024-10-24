package com.yunzia.hyperstar.hook.view;

// CustomDirectionDisk.java
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomDirectionDisk extends View {

    private Paint diskPaint;
    private Paint directionPaint;
    private Path directionPath;
    private RectF diskRect;
    private float radius;

    private float height;
    private float width;
    private float centerX;
    private float centerY;
    private String[] directions = {"Up", "Down", "Left", "Right"};
    private int selectedDirectionIndex = -1;
    private OnDirectionSelectedListener listener;

    // 构造函数
    public CustomDirectionDisk(Context context) {
        super(context);
        init();
    }

    public CustomDirectionDisk(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomDirectionDisk(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 初始化画笔和矩形
    private void init() {
        height = getHeight();
        width = getWidth();
        diskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        diskPaint.setColor(Color.LTGRAY);
        diskPaint.setStyle(Paint.Style.FILL);

        directionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        directionPaint.setColor(Color.BLACK);
        directionPaint.setTextSize(50);
        directionPaint.setTextAlign(Paint.Align.CENTER);

        directionPath = new Path();
        radius = 300;
        diskRect = new RectF(-width, -height, width, height);
    }

    // 设置方向选择监听器
    public void setOnDirectionSelectedListener(OnDirectionSelectedListener listener) {
        this.listener = listener;
    }

    // 方向选择监听器接口
    public interface OnDirectionSelectedListener {
        void onDirectionSelected(String direction);
    }

    // 绘制控件
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        centerX = width / 2f;
        centerY = height / 2f;

        // 绘制圆盘
        canvas.translate(centerX, centerY);
        canvas.drawCircle(0, 0, radius, diskPaint);
        canvas.translate(-centerX, -centerY);

        // 绘制方向指示（仅在选中时显示）
        if (selectedDirectionIndex != -1) {
            String direction = directions[selectedDirectionIndex];
            float textWidth = directionPaint.measureText(direction);
            float textX = centerX - textWidth / 2f;
            float textY = centerY + radius + 20; // 稍微偏移一些，以便文本不重叠圆盘

            canvas.drawText(direction, textX, textY, directionPaint);

            // 绘制方向路径（用于调试，可以注释掉）
            // 根据选中的方向绘制路径，这里只是示例，实际中可能不需要
            switch (selectedDirectionIndex) {
                case 0: // Up
                    directionPath.reset();
                    directionPath.moveTo(centerX, centerY - radius);
                    directionPath.lineTo(centerX, centerY - radius / 2);
                    canvas.drawPath(directionPath, directionPaint);
                    break;
                case 1: // Down
                    directionPath.reset();
                    directionPath.moveTo(centerX, centerY + radius);
                    directionPath.lineTo(centerX, centerY + radius / 2);
                    canvas.drawPath(directionPath, directionPaint);
                    break;
                case 2: // Left
                    directionPath.reset();
                    directionPath.moveTo(centerX - radius, centerY);
                    directionPath.lineTo(centerX - radius / 2, centerY);
                    canvas.drawPath(directionPath, directionPaint);
                    break;
                case 3: // Right
                    directionPath.reset();
                    directionPath.moveTo(centerX + radius, centerY);
                    directionPath.lineTo(centerX + radius / 2, centerY);
                    canvas.drawPath(directionPath, directionPaint);
                    break;
            }
        }
    }

    // 处理触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - centerX;
        float y = event.getY() - centerY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 根据触摸位置确定方向
                if (Math.abs(x) > Math.abs(y)) {
                    // 水平方向
                    if (x > this.width/4) {
                        selectedDirectionIndex = 3; // Right
                    } else if (x < -this.width/4){
                        selectedDirectionIndex = 2; // Left
                    }
                } else {
                    // 垂直方向
                    if (y > this.height/4) {
                        selectedDirectionIndex = 1; // Down
                    } else if (y < -this.height/4){
                        selectedDirectionIndex = 0; // Up
                    }
                }

                // 触发监听器（如果设置了的话）
                if (listener != null && selectedDirectionIndex != -1) {
                    listener.onDirectionSelected(directions[selectedDirectionIndex]);
                }

                // 请求重绘
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // 抬起手指时重置选择
                selectedDirectionIndex = -1;
                invalidate();
                return true;
        }

        return false;
    }
}
