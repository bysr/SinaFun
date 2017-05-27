package hipad.unittest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 环形进度条
 * Created by Wood on 2016/7/21.
 */
public class RingProgressBar extends View {
    private static final String LOG_TAG = "RingProgressBar";

    /**
     * 总值
     */
    private double max = 100;
    /**
     * 已使用
     */
    private double progress = 0;
    /**
     * 目标比例
     */
    private double aimAngle;
    /**
     * 进度条画笔
     */
    private Paint mPaint;
    private int strokeWidth = 12;
    private int wh;
    private int center;
    private int radius;
    private RectF oval;
    private Handler handler = new Handler();
    private int angle = -1;
    /**
     * 着色器，这里使用了一个扫描渲染器(SweepGradient)实现渐变色
     */
    private Shader shader;
    /**
     * 刻度背景图
     */
    private Bitmap bitmapBackground;

    /**
     * 开始的颜色
     */
    private int startColor = Color.parseColor("#303F9F");//开始颜色
    /**
     * 中间的颜色
     */
    private int centerColor = Color.parseColor("#FF4081");//中间颜色
    /**
     * 结束的颜色
     */
    private int endColor = Color.parseColor("#303F9F");//开始颜色

    /**
     * 进度条颜色数组
     */
    private int[] colors = new int[]{
            startColor,
            centerColor,
            endColor
    };
    /**
     * 刻度缩放比率
     */
    private float ratio = 1;

    public RingProgressBar(Context context) {
        this(context, null);
    }

    public RingProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar);
        startColor = a.getColor(R.styleable.RingProgressBar_startColor, startColor);
        centerColor = a.getColor(R.styleable.RingProgressBar_centerColor, centerColor);
        endColor = a.getColor(R.styleable.RingProgressBar_endColor, endColor);
        max = (double) a.getFloat(R.styleable.RingProgressBar_max, (float) max);
        progress = (double) a.getFloat(R.styleable.RingProgressBar_progress, (float) progress);
        a.recycle();
        colors = new int[]{startColor, centerColor, endColor};
        init();
        setValue(max, progress);
    }

    private void init() {
        bitmapBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.dial);
        strokeWidth = dip2px(12);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        aimAngle = -1;
    }

    /**
     * 画进度环
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            angle += 6;
            if (angle <= aimAngle) {
                postInvalidate();
                handler.postDelayed(runnable, 3);
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (progress > 0) {
            canvas.drawArc(oval, -90, angle, false, mPaint);
        }
        //根据空间大小缩放刻度背景图片
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        Bitmap bmp = Bitmap.createBitmap(bitmapBackground, 0, 0, bitmapBackground.getWidth(), bitmapBackground.getHeight(), matrix, true);
        canvas.drawBitmap(bmp, 0, 0, mPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Log.e(LOG_TAG, "width:" + width + "...height:" + height);
        Log.e(LOG_TAG, "bitmapBackground.width:" + bitmapBackground.getWidth() + "...bitmapBackground.height:" + bitmapBackground.getHeight());

        wh = Math.min(width, height);
        if (bitmapBackground.getWidth() != 0 && wh != 0) {
            ratio = (float) wh / (float) bitmapBackground.getWidth();
        }
        center = wh / 2;
        radius = center - strokeWidth / 2;
        oval = new RectF(center - radius, center - radius, center + radius, center + radius);
        shader = new SweepGradient(center, center, colors, null);
        Matrix m = new Matrix();
        m.setRotate(-90, center, center);
        shader.setLocalMatrix(m);
        mPaint.setShader(shader);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 释放bitmap资源
     */
    public void clearBitmap() {
        if (bitmapBackground != null && !bitmapBackground.isRecycled()) {
            bitmapBackground.recycle();
        }
    }

    /**
     * 设置目标比率
     *
     * @param max
     * @param progress
     */
    public void setValue(double max, double progress) {
        this.max = max;
        this.progress = progress;
        if (max != 0f) {
            aimAngle = progress / max * 360;
        }
        angle = -1;
        handler.postDelayed(runnable, 400);
    }

    /**
     * 获取最大进度，默认100
     *
     * @return
     */
    public double getMax() {
        return max;
    }

    /**
     * 设置最大进度
     *
     * @param max
     */
    public void setMax(double max) {
        if (max > 0) {
            this.max = max;
        }
    }

    /**
     * 获取当前进度，默认0
     *
     * @return
     */
    public double getProgress() {
        return progress;
    }

    /**
     * 设置当前进度
     *
     * @param progress
     */
    public void setProgress(double progress) {
        if (progress >= 0) {
            this.progress = progress;
        }
        if (max >= 0 && max >= progress) {
            setValue(max, progress);
        }
    }

    /**
     * dp转px
     *
     * @param dip
     * @return
     */
    private int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        int px = (int) (dip * density + 0.5f);
        return px;
    }
}
