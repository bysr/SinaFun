package hipad.animal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class SineWave extends View implements Runnable {

    private Paint mPaint = null;

    private float amplifier = 100.0f;

    private float frequency = 2.0f;    //2Hz

    private float phase = 0.0f;         //相位

    private int height = 0;

    private int width = 0;

    private static float px = -1, py = -1;

    private boolean sp = false;


    public SineWave(Context context) {

        super(context);

        mPaint = new Paint();

        new Thread(this).start();

    }

    //如果不写下面的构造函数，则会报错：custom view SineWave is not using the 2- or 3-argument View constructors

    public SineWave(Context context, AttributeSet attrs) {

        super(context, attrs);

        mPaint = new Paint();

        new Thread(this).start();

    }

    public SineWave(Context context, float amplifier, float frequency, float phase) {

        super(context);

        this.frequency = frequency;

        this.amplifier = amplifier;

        this.phase = phase;

        mPaint = new Paint();

        new Thread(this).start();

    }

    public float GetAmplifier() {

        return amplifier;

    }

    public float GetFrequency() {

        return frequency;

    }

    public float GetPhase() {

        return phase;

    }

    public void Set(float amplifier, float frequency, float phase) {

        this.amplifier = amplifier;
        this.frequency = frequency;
        this.phase = phase;

    }


    public void SetXY(float px, float py) {
        this.px = px;
        this.py = py;

    }

    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

//        canvas.drawColor(Color.WHITE);

        height = this.getHeight();

        width = this.getWidth();

        mPaint.setAntiAlias(true);

        mPaint.setColor(Color.GREEN);

        amplifier = (amplifier * 2 > height) ? (height / 2) : amplifier;

        mPaint.setAlpha(200);

        mPaint.setStrokeWidth(5);

        float cy = height / 2;

        //float py=this.py-this.getTop();

        for (int i = 0; i < width - 1; i++)

        {

            canvas.drawLine((float) i, cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * i / width)), (float) (i + 1), cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * (i + 1) / width)), mPaint);

            float point = cy - amplifier * (float) (Math.sin(phase * 2 * (float) Math.PI / 360.0f + 2 * Math.PI * frequency * i / width));

            if ((py >= (point - 2.5f)) && (py <= (point + 2.5f)) && (px >= i - 2.5f) && (px <= i + 2.5f))

                sp = true;

        }

        if (sp)

        {

            mPaint.setColor(Color.RED);

            mPaint.setTextSize(20);

            canvas.drawText("(x=" + Float.toString(px) + ",y=" + Float.toString(py) + ")", 20, 20, mPaint);

            sp = false;

        }

        mPaint.setColor(Color.BLUE);

        mPaint.setTextSize(20);

        canvas.drawText("(x=" + Float.toString(px) + ",y=" + Float.toString(py) + ")", 20, this.getHeight() - 20, mPaint);

    }


    @Override

    public boolean onTouchEvent(MotionEvent event) {

        // TODO Auto-generated method stub

        float px = event.getX();

        float py = event.getY();

        this.SetXY(px, py);

        return super.onTouchEvent(event);

    }

    @Override

    public void run() {

        // TODO Auto-generated method stub

        while (!Thread.currentThread().isInterrupted())

        {

            try {

                Thread.sleep(1000);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

            }

            postInvalidate();

        }

    }


}
