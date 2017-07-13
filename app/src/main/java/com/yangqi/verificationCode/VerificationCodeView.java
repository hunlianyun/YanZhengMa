package com.yangqi.verificationCode;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.Random;

/**
 * Created by Administrator on 2017/3/23.
 */
public class VerificationCodeView extends View {

    /**
     * View 要显示的验证码
     */
    private String mText;
    private int mComplex;
    private int mTextColor;
    private int mBackgroundColor;
    private int mTextSize;
    private Paint mPaint;
    private Rect mBounds;
    private static final String TAG = "MyView";
    private Random random;

    public VerificationCodeView(Context context) {
        this(context, null);
    }

    public VerificationCodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int dimension = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics());
        // 设置各控件的默认属性
        initData();
        mText = getRandom4number();
        mComplex = 12;
        mBackgroundColor = Color.GRAY;
        mTextColor = Color.YELLOW;
        mTextSize = dimension;

        Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs, R.styleable.CustomStyle, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomStyle_complex:
                    mComplex = a.getInt(attr, 12);
                    if (mComplex < 0) {
                        mComplex = 12;
                    }
                    break;
                case R.styleable.CustomStyle_background_color:
                    mBackgroundColor = a.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.CustomStyle_text_color:
                    mTextColor = a.getColor(attr, Color.YELLOW);
                    break;
                case R.styleable.CustomStyle_text_size:
                    mTextSize = a.getDimensionPixelSize(attr, dimension);
                    break;
            }
        }
        a.recycle();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VerificationCodeView.this.mText = getRandom4number();
                invalidate();
            }
        });
    }

    /**
     * 初始化各个成员变量，因 onDraw() 方法里面不能 new 对象，
     * 所以在构造方法里面就需要初始化
     */
    private void initData() {
        random = new Random();
        mPaint = new Paint();
        mBounds = new Rect();
    }

    /**
     * 获取 Text 内容的大小存入 mBounds
     */
    private void setTextBounds() {
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);
        Log.i(TAG, "setTextBounds mText : " + mText);
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            setTextBounds();
            width = mBounds.width() + 30;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            setTextBounds();
            height = mBounds.height() + 30;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // 画背景
        mPaint.setColor(mBackgroundColor);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        // 画文字
        setTextBounds();
        float x = getMeasuredWidth() / 2.0f - mBounds.width() / 2.0f;
        float y = getMeasuredHeight() / 2.0f + mBounds.height() / 2.0f;
        canvas.drawText(mText, x, y, mPaint);

        // 画噪点，随机画几条线和一个随机位置的半透明圆模拟
        random.setSeed(System.currentTimeMillis());
        float startX, stopX, startY, stopY;
        int alpha, red, green, blue;
        float radius;
        for (int i = 0; i < mComplex; i++) {
            red = random.nextInt(256);
            green = random.nextInt(256);
            blue = random.nextInt(256);
            startX = random.nextFloat() * getMeasuredWidth();
            stopX = random.nextFloat() * getMeasuredWidth();
            startY = random.nextFloat() * getMeasuredHeight();
            stopY = random.nextFloat() * getMeasuredHeight();

            alpha = random.nextInt(200);
            mPaint.setColor(Color.argb(alpha, red, green, blue));
            radius = random.nextFloat() * getMeasuredHeight() / 2.0f;
            canvas.drawCircle(startX, stopY, radius, mPaint);

            mPaint.setAlpha(255);
            red = random.nextInt(256);
            green = random.nextInt(256);
            blue = random.nextInt(256);
            mPaint.setColor(Color.argb(alpha, red, green, blue));
            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
        }
    }

    /**
     * 产生随机四位数字（验证码是四位的）
     *
     * @return
     */
    private String getRandom4number() {
        StringBuilder sb = new StringBuilder();
        random.setSeed(System.currentTimeMillis());
        int i = 0;
        while (i < 4) {
            sb.append(random.nextInt(10));
            i++;
        }
        return sb.toString();
    }

    public String getText() {
        return mText;
    }

    public int getmComplex() {
        return mComplex;
    }

    public void setmComplex(int mComplex) {
        this.mComplex = mComplex;
        invalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        invalidate();
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        invalidate();
    }

    public int getTextSize() {
        return mTextSize;
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        invalidate();
    }

}
