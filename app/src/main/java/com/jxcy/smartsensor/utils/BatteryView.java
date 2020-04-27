package com.jxcy.smartsensor.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.jxcy.smartsensor.R;


/**
 * 自定义view  电池电量条
 */

public class BatteryView extends View {

    private float mPower = 60;
    private int battery_width = 0;
    private int battery_height = 0;
    private int battery_head_width = 3;
    private int battery_head_height = 3;

    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.BatteryView);
        battery_height = (int) array.getDimension(R.styleable.BatteryView_height, 0);
        battery_width = (int) array.getDimension(R.styleable.BatteryView_width, 0);
        battery_head_height = (int) array.getDimension(R.styleable.BatteryView_head_height, 0);
        battery_head_width = (int) array.getDimension(R.styleable.BatteryView_head_width, 0);
        array.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int battery_inside_margin = 5;

        //先画外框
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1.5f);
        paint.setStyle(Paint.Style.STROKE);

        Rect rect = new Rect(0, 0, battery_width, battery_height);
        canvas.drawRect(rect, paint);

        float power_percent = mPower / 100.0f;

        Paint paint2 = new Paint(paint);
        if(mPower>60){
            paint2.setColor(Color.WHITE);
        }else if(mPower>35){
            paint2.setColor(Color.YELLOW);
        }else {
            paint2.setColor(Color.RED);
        }
        paint2.setStyle(Paint.Style.FILL);
        //画电量
        if (power_percent != 0) {
            int p_left = battery_inside_margin;
            int p_top = battery_inside_margin;
            int p_right = (int) (battery_width * power_percent - battery_inside_margin);
            int p_bottom = battery_height - battery_inside_margin;
            Rect rect2 = new Rect(p_left, p_top, p_right, p_bottom);
            canvas.drawRect(rect2, paint2);
        }

        //画电池头
        int h_left = battery_width;
        int h_top = battery_height / 2 - battery_head_height / 2;
        int h_right = h_left + battery_head_width;
        int h_bottom = h_top + battery_head_height;
        Rect rect3 = new Rect(h_left, h_top, h_right, h_bottom);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        paint.setStyle(Paint.Style.STROKE);
        paint2.setColor(Color.WHITE);
        canvas.drawRect(rect3, paint2);
    }

    public void setPower(int power) {
        mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        invalidate();
    }
}

