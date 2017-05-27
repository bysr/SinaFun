package hipad.unittest;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 自动滑动一个View的ScrollView
 * Created by Wood on 2016/7/22.
 */
public class AutoScrollView extends ViewGroup {
    private static final String LOG_TAG = "AutoScrollView";

    /**
     * 辅助平滑滚动的Helper类
     */
    private Scroller scroller;
    /**
     * 屏幕高度
     */
    private int screenHeight;
    /**
     * 按下时的y坐标
     */
    private int lastY;
    /**
     * 滑动开始的位置
     */
    private int start;
    /**
     * 滑动结束的位置
     */
    private int end;
    /**
     * 控件高度
     */
    private int viewGroupHeight;

    public AutoScrollView(Context context) {
        this(context, null);
    }

    public AutoScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 取得窗口属性
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;

        scroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        //设置控件高度
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) getLayoutParams();
        marginLayoutParams.height = screenHeight * count;
        viewGroupHeight = screenHeight * count;
        Log.e(LOG_TAG, "count:" + count + "...height:" + marginLayoutParams.height);
        setLayoutParams(marginLayoutParams);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                child.layout(l, i * screenHeight, r, (i + 1) * screenHeight);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!scroller.isFinished()) {
            //滑动结束才能下一次滑动
            return true;
        }
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(LOG_TAG, "lastY:" + lastY + "...start:" + start);
                lastY = y;//记录按下的y坐标，跟随滑动使用
                start = getScrollY();//记录触摸的起点，释放滚动使用
                break;
            case MotionEvent.ACTION_MOVE:
                if (!scroller.isFinished()) {
                    return true;
                    //scroller.abortAnimation();
                }
                int dy = lastY - y;
                if (dy > 200 || dy < -200) {
                    dy = 0;
                }
                if (-getScrollY() > screenHeight / 3 - 100 || getScrollY() > viewGroupHeight - screenHeight / 3 * 2 - 100) {
                    dy = 0;
                }
                Log.e(LOG_TAG, "dy:" + dy + "...getScrollY():" + getScrollY() + "...getHeight():" + getHeight() + "...screenHeight" + screenHeight);
                if (getScrollY() < 0 || getScrollY() > viewGroupHeight - screenHeight) {
                    scrollBy(0, dy / 3);
                } else {
                    scrollBy(0, dy);
                }
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (getScrollY() < 0) {
//                    scrollTo(0, 0);
                    scroller.startScroll(0, getScrollY(), 0, 0);

                }
                if (getScrollY() > viewGroupHeight - screenHeight) {
//                    scrollTo(0, viewGroupHeight - screenHeight);
                    scroller.startScroll(0, getScrollY(), 0, viewGroupHeight - screenHeight);
                }

                end = getScrollY();
                int dScrollY = end - start;
                Log.e(LOG_TAG, "dScrollY:" + dScrollY + "...end:" + end);
                if (dScrollY > 0) {
                    if (dScrollY < screenHeight / 3) {
                        scroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    } else {
                        scroller.startScroll(0, getScrollY(), 0, screenHeight - dScrollY);
                    }
                } else {
                    if (-dScrollY < screenHeight / 3) {
                        scroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    } else {
                        scroller.startScroll(0, getScrollY(), 0, -screenHeight - dScrollY);
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.e(LOG_TAG, "MotionEvent.ACTION_POINTER_DOWN");
                break;
        }
        postInvalidate();
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(0, scroller.getCurrY());
            postInvalidate();
        }
    }
}
