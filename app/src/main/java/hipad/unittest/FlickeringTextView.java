package hipad.unittest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * 闪烁的TextView
 * Created by Wood on 2016/7/5.
 */
public class FlickeringTextView extends TextView {
    private static final String LOG_TAG = "FlickeringTextView";

    private int mViewWidth;
    private TextPaint mPaint;
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mTranslate;

    private int textColor = 0xFFFFFFFF;//文字颜色
    private int flickeringColor = 0xFF000000;//闪烁颜色

    private static final int[] ATTRS = new int[]{
            android.R.attr.textColor,
    };

    public FlickeringTextView(Context context) {
        this(context, null);
    }

    public FlickeringTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlickeringTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //先获取系统属性，文字颜色
        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        textColor = a.getColor(0, textColor);
        a.recycle();
        //再获取自定义属性，闪烁颜色
        a = context.obtainStyledAttributes(attrs, R.styleable.FlickeringTextView);
        flickeringColor = a.getColor(R.styleable.FlickeringTextView_flickeringColor, flickeringColor);
        a.recycle();
        Log.e(LOG_TAG, "3textColor：" + textColor + ";flickeringColor：" + flickeringColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /*Paint mPaint1 = new Paint();
        mPaint1.setColor(getResources().getColor(android.R.color.holo_blue_light));
        mPaint1.setStyle(Paint.Style.FILL);
        Paint mPaint2 = new Paint();
        mPaint2.setColor(Color.YELLOW);
        mPaint2.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint1);
        canvas.drawRect(10, 10, getMeasuredWidth() - 10, getMeasuredHeight() - 10, mPaint2);
        canvas.save();
        //canvas.translate(10,0);
        super.onDraw(canvas);
        canvas.restore();*/

        super.onDraw(canvas);
        if (mGradientMatrix != null) {
            mTranslate += mViewWidth / 5;
            if (mTranslate > mViewWidth * 2) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            postInvalidateDelayed(100);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(
                        0,//渐变起初点坐标x位置
                        0,//渐变起初点坐标y位置
                        mViewWidth,//渐变终点
                        0,//渐变终点
                        new int[]{textColor, flickeringColor, textColor},//参与渐变效果的颜色集合
                        null,//每个颜色处于的渐变相对位置
                        Shader.TileMode.CLAMP//平铺方式
                );
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
            }
        }
    }
}
