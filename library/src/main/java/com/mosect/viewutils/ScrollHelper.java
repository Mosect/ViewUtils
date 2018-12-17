package com.mosect.viewutils;

import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewGroup;

/**
 * 滑动辅助器，可以直接使用{@link ViewScrollHelper ViewScrollHelper}
 */
public abstract class ScrollHelper {

    private GestureHelper gestureHelper;
    private VelocityTracker velocityTracker;

    private float startTouchX;
    private float startTouchY;
    private int startScrollX;
    private int startScrollY;

    public ScrollHelper(GestureHelper gestureHelper) {
        this.gestureHelper = gestureHelper;
        this.velocityTracker = VelocityTracker.obtain();
    }

    public ScrollHelper(Context context) {
        this(GestureHelper.createDefault(context));
    }

    /**
     * 触发触摸事件
     *
     * @param event 事件
     */
    public void onTouchEvent(MotionEvent event) {
        gestureHelper.onTouchEvent(event);
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startTouchX = event.getX();
                startTouchY = event.getY();
                startScrollX = getViewScrollX();
                startScrollY = getViewScrollY();
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (gestureHelper.isVerticalGesture() || gestureHelper.isHorizontalGesture()) {
                    float rangeX = event.getX() - startTouchX;
                    float rangeY = event.getY() - startTouchY;
                    int dstX = (int) (startScrollX - rangeX);
                    int dstY = (int) (startScrollY - rangeY);
                    if (dstX < 0) {
                        dstX = 0;
                        startTouchX = event.getX();
                        startScrollX = dstX;
                    } else if (dstX > getViewHorizontallyScrollSize()) {
                        dstX = getViewHorizontallyScrollSize();
                        startTouchX = event.getX();
                        startScrollX = dstX;
                    }
                    if (dstY < 0) {
                        dstY = 0;
                        startTouchY = event.getY();
                        startScrollY = dstY;
                    } else if (dstY > getViewVerticallyScrollSize()) {
                        dstY = getViewVerticallyScrollSize();
                        startTouchY = event.getY();
                        startScrollY = dstY;
                    }
                    viewScrollTo(dstX, dstY);
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                velocityTracker.computeCurrentVelocity(1000);
                float xv = velocityTracker.getXVelocity();
                float yv = velocityTracker.getYVelocity();
                viewFling(xv, yv);
                break;
            }
        }
    }

    /**
     * 获取视图滑动位置X
     *
     * @return 视图滑动位置Y
     */
    protected abstract int getViewScrollX();

    /**
     * 获取视图滑动位置Y
     *
     * @return 视图滑动位置Y
     */
    protected abstract int getViewScrollY();

    /**
     * 获取视图水平方向可以滑动的范围，一般在此方法返回
     * {@link ViewGroup#computeHorizontalScrollRange() ViewGroup.computeHorizontalScrollRange} 减去
     * {@link ViewGroup#computeHorizontalScrollExtent() ViewGroup.computeHorizontalScrollExtent} 的差
     * <br>result = range-extent
     *
     * @return 水平方向可以滑动的范围
     */
    protected abstract int getViewHorizontallyScrollSize();

    /**
     * 获取视图垂直方向可以滑动的范围，一般在此方法返回
     * {@link ViewGroup#computeVerticalScrollRange() ViewGroup.computeVerticalScrollRange} 减去
     * {@link ViewGroup#computeVerticalScrollExtent() ViewGroup.computeVerticalScrollExtent} 的差
     * <br>result = range-extent
     *
     * @return 垂直方向可以滑动的范围
     */
    protected abstract int getViewVerticallyScrollSize();

    /**
     * 将视图滑动至指定位置，一般调用{@link android.view.View#scrollTo(int, int) View.scrollTo}方法即可
     *
     * @param x 位置X
     * @param y 位置Y
     */
    protected abstract void viewScrollTo(int x, int y);

    /**
     * 当触摸抬起时，执行此方法，一般在此方法内执行
     * {@link android.widget.Scroller#fling(int, int, int, int, int, int, int, int) Scroller.fling}
     * 方法，需要注意的是，速度应该取参数的相反值，因为参数的速度表示的是触摸滑动的速度，刚好与滑动
     * 的速度方向相反。
     *
     * @param xv 水平触摸滑动的速度
     * @param yv 垂直触摸滑动的速度
     */
    protected abstract void viewFling(float xv, float yv);
}
