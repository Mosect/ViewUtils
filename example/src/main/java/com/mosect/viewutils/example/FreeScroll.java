package com.mosect.viewutils.example;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.mosect.viewutils.InterceptTouchHelper;
import com.mosect.viewutils.MeasureUtils;
import com.mosect.viewutils.ScrollHelper;
import com.mosect.viewutils.ViewScrollHelper;

/**
 * 自由滑动视图，此视图类似垂直LinerLayout，可以嵌套可滑动视图。
 * 主要使用了{@link ScrollHelper ScrollHelper}、{@link MeasureUtils MeasureUtils}和
 * {@link InterceptTouchHelper InterceptTouchHelper}实现。
 */
public class FreeScroll extends FrameLayout {

    private InterceptTouchHelper interceptTouchHelper; // 拦截辅助器
    private Scroller scroller; // 滑动器
    private ScrollHelper scrollHelper; // 滑动辅助器
    private int contentWidth; // 内容宽度
    private int contentHeight; // 内容高度

    private Rect layoutRect = new Rect(); // 子视图布局
    private Rect spaceRect = new Rect(); // 子视图占用空间

    public FreeScroll(@NonNull Context context) {
        super(context);
        init();
    }

    public FreeScroll(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FreeScroll(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        interceptTouchHelper = new InterceptTouchHelper(this);
        scroller = new Scroller(getContext());

        scrollHelper = new ViewScrollHelper(this) {
            @Override
            protected int getViewHorizontallyScrollSize() {
                return computeHorizontalScrollRange() - computeHorizontalScrollExtent();
            }

            @Override
            protected int getViewVerticallyScrollSize() {
                return computeVerticalScrollRange() - computeVerticalScrollExtent();
            }

            @Override
            protected void viewFling(float xv, float yv) {
                // 触摸滑动抬起时，需要进行fling操作，注意：xv和yv应该取反值。
                scroller.fling(getScrollX(), getScrollY(), (int) -xv, (int) -yv, 0,
                        getViewHorizontallyScrollSize(), 0, getViewVerticallyScrollSize());
                invalidate();
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int horizontalPadding = getPaddingLeft() + getPaddingRight(); // 水平内间距
        int verticalPadding = getPaddingTop() + getPaddingBottom(); // 垂直内间距

        // 先制作出属于自己的测量规格，对内间距进行处理
        int selfWidthMeasureSpec =
                MeasureUtils.makeSelfMeasureSpec(widthMeasureSpec, horizontalPadding);
        int selfHeightMeasureSpec =
                MeasureUtils.makeSelfMeasureSpec(heightMeasureSpec, verticalPadding);

        // 重置一些变量值
        int viewWidth = 0, viewHeight = 0; // 视图的宽，高
        contentHeight = 0; // 内容的高

        // 逐个计算子视图的大小
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue; // gone的视图不计算
            // 测量子视图，应该将自己的测量规格传过去
            MeasureUtils.measureChild(child, selfWidthMeasureSpec, selfHeightMeasureSpec);
            // 选出最大的宽和高作为视图的宽和高
            if (child.getMeasuredWidth() > viewWidth) {
                viewWidth = child.getMeasuredWidth();
            }
            if (child.getMeasuredHeight() > viewHeight) {
                viewHeight = child.getMeasuredHeight();
            }
            // 因为要实现垂直的线性布局，高应该是叠加，可以使用MeasureUtils.getViewHeightSpace
            // 计算出子视图占用的高度（对margin进行了处理）
            contentHeight += MeasureUtils.getViewHeightSpace(child);
        }

        // 别忘记，视图的宽度应该加上内边距
        viewWidth += horizontalPadding;
        viewHeight += verticalPadding;

        // 因为是垂直线性布局，所以内容宽度和视图宽度相等。
        contentWidth = viewWidth;

        // 计算出视图的宽度和内容的宽度，不应该使用他直接设置为测量宽和高，应该使用
        // MeasureUtils.getMeasuredDimension获取安全的大小，再设置未测量的宽和高
        // 此时的测量规格，应该是onMeasure方法传来的测量规格，因为视图本身的大小，会被父视图限制。
        // MeasureUtils.getMeasuredDimension主要根据父视图限制计算出自身安全的尺寸大小。
        int width = MeasureUtils.getMeasuredDimension(viewWidth, widthMeasureSpec);
        int height = MeasureUtils.getMeasuredDimension(viewHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int x = getPaddingLeft();
        int y = getPaddingTop();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;
            MeasureUtils.computeLayout(child, x, y, layoutRect, spaceRect);
            child.layout(layoutRect.left, layoutRect.top, layoutRect.right, layoutRect.bottom);
            y = spaceRect.bottom;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = super.onInterceptTouchEvent(ev);
        boolean ext = interceptTouchHelper.onInterceptTouchEvent(ev);
        return result && ext;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            scroller.abortAnimation();
            performClick();
        }
        scrollHelper.onTouchEvent(event);
        return true;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        return contentWidth;
    }

    @Override
    protected int computeVerticalScrollRange() {
        return contentHeight;
    }

    @Override
    protected int computeHorizontalScrollExtent() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected int computeVerticalScrollExtent() {
        return getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
