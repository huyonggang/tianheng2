package com.tianheng.client.wedget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author kince
 * @category 自定义View电池
 * @time 2014.2.13
 * @qq 543781062
 */
public class BatteryView extends View {

    /**
     * 画笔信息
     */
    private Paint mBatteryPaint;
    private Paint mPowerPaint;
    private float mBatteryStroke = 2f;
    /**
     * 屏幕高宽
     */
    private int measureWidth;
    private int measureHeigth;
    /**
     * 电池参数
     */
    private float mBatteryHeight = 60f; // 电池的高度
    private float mBatteryWidth = 240f; // 电池的宽度
    private float mCapHeight = 15f;
    private float mCapWidth = 5f;
    /**
     * 电池电量
     */
    private float mPowerPadding = 1;
    private float mPowerHeight = mBatteryHeight - mBatteryStroke
            - mPowerPadding * 2; // 电池身体的高度
    private float mPowerWidth = mBatteryWidth - mBatteryStroke - mPowerPadding
            * 2;// 电池身体的总宽度
    private float mPower = 10f;
    /**
     * 矩形
     */
    private RectF mBatteryRect;
    private RectF mCapRect;
    private RectF mPowerRect;

    public BatteryView(Context context) {
        super(context);
        initView();
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        /**
         * 设置电池画笔
         */
        mBatteryPaint = new Paint();
        mBatteryPaint.setColor(Color.GRAY);
        mBatteryPaint.setAntiAlias(true);
        mBatteryPaint.setStyle(Style.STROKE);
        mBatteryPaint.setStrokeWidth(mBatteryStroke);
        /**
         * 设置电量画笔
         */
        mPowerPaint = new Paint();
        mPowerPaint.setColor(Color.RED);
        mPowerPaint.setAntiAlias(true);
        mPowerPaint.setStyle(Style.FILL);
        mPowerPaint.setStrokeWidth(mBatteryStroke);
        /**
         * 设置电池矩形
         */
        mBatteryRect = new RectF(mCapWidth, 0, mBatteryWidth, mBatteryHeight);
        /**
         * 设置电池盖矩形
         */
        mCapRect = new RectF(mBatteryWidth, (mBatteryHeight - mCapHeight) / 2, mCapWidth + mBatteryWidth,
                (mBatteryHeight - mCapHeight) / 2 + mCapHeight);
        /**
         * 设置电量矩形
         */
        mPowerRect = new RectF(mBatteryStroke * 4, // 需要调整左边的位置
                mPowerPadding + mBatteryStroke / 2, // 需要考虑到 画笔的宽度
                mCapWidth + mBatteryStroke / 2 + mPowerPadding
                        + mPowerWidth * (mPower / 100f), mBatteryStroke / 2
                + mPowerPadding + mPowerHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(80, 18);
        canvas.drawRoundRect(mBatteryRect, 2f, 2f, mBatteryPaint); // 画电池轮廓需要考虑 画笔的宽度
        canvas.drawRoundRect(mCapRect, 2f, 2f, mBatteryPaint);// 画电池盖
        canvas.drawRect(mPowerRect, mPowerPaint);// 画电量
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeigth);
    }

    /**
     * ]
     *
     * @param power
     * @category 设置电池电量
     */
    public void setPower(float power) {
        mPower = power;
        if (mPower < 0) {
            mPower = 0;
        }
        mPowerRect = new RectF(mBatteryStroke * 4, // 需要调整左边的位置
                mPowerPadding + mBatteryStroke / 2, // 需要考虑到 画笔的宽度
                mCapWidth + mBatteryStroke / 2 + mPowerPadding
                        + mPowerWidth * (mPower / 100f), mBatteryStroke / 2
                + mPowerPadding + mPowerHeight);
        invalidate();
    }

}
