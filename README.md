# ViewUtils
视图工具类，提供了三个视图辅助器：
* GestureHelper（手势辅助器）
* InterceptTouchHelper（拦截辅助器）
* ScrollHelper（滑动辅助器）  

此外，还提供了测量工具：
* MeasureUtils

## 使用
### Gradle
在项目根节点的build.gradle中配置：
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
在需要引入的项目build.gradle中添加
```
implementation 'com.github.mosect:ViewUtils:1.0.8'
```

## GestureHelper
手势辅助器，主要是判断手势的，可以在View.onTouchEvent方法中使用：\
```
public boolean onTouchEvent(MotionEvent event) { 
gestureHelper.onTouchEvent(event);
switch (gestureHelper.getGesture()) {
    case GestureHelper.GESTURE_XX: // 判断手势
        // todo 做手势相应的处理
        break;
    }
    return true;
}
```
提供了7种手势：
```
/**
 * 手势：按住
 */
public static final int GESTURE_PRESSED = 1;
/**
 * 手势：点击
 */
public static final int GESTURE_CLICK = 2;
/**
 * 手势：长按
 */
public static final int GESTURE_LONG_CLICK = 3;
/**
 * 手势：左滑
 */
public static final int GESTURE_LEFT = 4;
/**
 * 手势：上滑
 */
public static final int GESTURE_UP = 5;
/**
 * 手势：右滑
 */
public static final int GESTURE_RIGHT = 6;
/**
 * 手势：下滑
 */
public static final int GESTURE_DOWN = 7;
```
**注意：此辅助器不是回调方式，需要主动去获取手势进行判断，执行相应操作。**

## InterceptTouchHelper
拦截辅助器，主要用于ViewGroup拦截滑动事件，只需在ViewGroup.onInterceptTouchEvent方法中使用：
```
@Override
public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean result = super.onInterceptTouchEvent(ev);
    boolean ext = interceptTouchHelper.onInterceptTouchEvent(ev);
    return result && ext;
}
```
此外，你还可以继承此类，复写其中一些方法，达到自己所需要求。
```
/**
 * 判断子视图是否可以垂直滑动
 *
 * @param event     滑动事件
 * @param direction 方向：负数表示ScrollY值变小的方向；整数表示ScrollY值变大的方向
 * @return true，子View可以滑动
 */

protected boolean canChildrenScrollVertically(MotionEvent event, int direction) {...}
/**
 * 判断子View是否可以垂直滑动
 *
 * @param child     子View
 * @param direction 方向：负数表示ScrollY值变小的方向；整数表示ScrollY值变大的方向
 * @return true，可以滑动
 */
protected boolean canChildScrollVertically(View child, int direction) {...}

/**
 * 判断子视图是否可以水平滑动
 *
 * @param event     滑动事件
 * @param direction 方向：负数表示ScrollX值变小的方向；整数表示ScrollX值变大的方向
 * @return true，子View可以滑动
 */
protected boolean canChildrenScrollHorizontally(MotionEvent event, int direction) {...}

/**
 * 判断子View是否可以水平滑动
 *
 * @param child     子View
 * @param direction 方向：负数表示ScrollX值变小的方向；整数表示ScrollX值变大的方向
 * @return true，可以滑动
 */
protected boolean canChildScrollHorizontally(View child, int direction) {...}

/**
 * 判断父视图是否可以水平滑动
 *
 * @param parent    父视图
 * @param direction 方向
 * @return true，可以水平滑动
 */
protected boolean canParentScrollHorizontally(ViewGroup parent, int direction) {...}

/**
 * 判断父视图是否可以垂直滑动
 *
 * @param parent    父视图
 * @param direction 方向
 * @return true，可以水平滑动
 */
protected boolean canParentScrollVertically(ViewGroup parent, int direction) {...}
```
**注意：此辅助器会优先判断子视图是否需要滑动事件，如果需要滑动事件，则辅助器直接返回false。**

## ScrollHelper
滑动辅助器，主要是辅助视图进行滑动，可以使用ViewScrollHelper，里面实现了部分方法，具体使用：
```
@Override
public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) { // 按下时，需要做一些处理
        scroller.abortAnimation(); // 停止动画
        performClick(); // 触发点击事件，推荐这么写，不这么写，会有警告（此外，还需复写performClick，才能完全消除警告）
    }
    scrollHelper.onTouchEvent(event); // 直接调用辅助器的onTouchEvent方法
    return true;
}
```
此辅助类是抽象类，有一些方法必须在视图内部才能实现，因此，创建时，应该这么写：
```
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
```
getViewHorizontallyScrollSize和getViewVerticallyScrollSize，表示的是视图可以滑动的范围，一般都是range-extent，不过可以自己根据实际情况，返回正确的值。  
**注意：在viewFling方法中，速度应该是取相反值，因为参数中的速度表示的是触摸滑动的速度，与滑动的速度方向刚好相反。**

## MeasureUtils
测量工具，提供了一些有关测量、布局相关计算，对内间距（padding）和外间距（margin）的处理。使用方法，大概有以下几步：
```
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
    ...

    // 逐个计算子视图的大小
    for (int i = 0; i < getChildCount(); i++) {
        View child = getChildAt(i);
        if (child.getVisibility() == GONE) continue; // gone的视图不计算
        // 测量子视图，应该将自己的测量规格传过去
        MeasureUtils.measureChild(child, selfWidthMeasureSpec, selfHeightMeasureSpec);
        // 其他一些关于自身的额外操作、计算
        ...
    }

    // 计算视图的大小，别忘记，视图的宽度应该加上内边距
    viewWidth += horizontalPadding;
    viewHeight += verticalPadding;

    // 计算出视图的宽度和内容的宽度，不应该使用他直接设置为测量宽和高，应该使用
    // MeasureUtils.getMeasuredDimension获取安全的大小，再设置未测量的宽和高
    // 此时的测量规格，应该是onMeasure方法传来的测量规格，因为视图本身的大小，会被父视图限制。
    // MeasureUtils.getMeasuredDimension主要根据父视图限制计算出自身安全的尺寸大小。
    int width = MeasureUtils.getMeasuredDimension(viewWidth, widthMeasureSpec);
    int height = MeasureUtils.getMeasuredDimension(viewHeight, heightMeasureSpec);
    setMeasuredDimension(width, height);
}
```
此外，此工具还提供了三个有关于布局的方法：
```
/**
 * 计算视图占用的宽度
 *
 * @param view 视图
 * @return 视图占用的宽度
 */
public static int getViewWidthSpace(View view) {...}

/**
 * 计算视图占用的高度
 *
 * @param view 视图占用的高度
 * @return 视图占用的高度
 */
public static int getViewHeightSpace(View view) {...}

/**
 * 计算布局所需的值
 *
 * @param view      视图
 * @param layoutX   布局的位置X
 * @param layoutY   布局的位置Y
 * @param layoutOut 返回布局的位置
 * @param spaceOut  返回占用的位置
 */
public static void computeLayout(View view, int layoutX, int layoutY, Rect layoutOut, Rect spaceOut) {...}
```
更多具体使用方法，可以查看示例。  

# 更新记录
## 1.0.6
### 更改：
* 修复在视图位置不固定的情况下，获取的手势不正确的问题

## 1.0.4
### 更改
* 删除不必要打印信息
### 问题
* 在视图位置不固定的情况下，获取的手势不正确，已在V1.0.6中修复

## 1.0.3
### 修复
* 修复ScrollHelper中canScroll判断不正确问题，并添加默认的判断规则

## 1.0.2
### 更改
* 向外部提供手势辅助器，增加getGestureHelper方法
* 内部提供canScroll方法，可以根据实际情况返回是否需要滑动
### 问题
* ScrollHelper中，canScroll方法判断不正确问题，已在1.0.3中修复

## 1.0.0
### 问题
* ScrollHelper没有提供手势帮助器，已在1.0.2版本提供（getGestureHelper）
* ScrollHelper没有提供根据实际情况决定是否滑动的方法，已在1.0.2版本提供（canScroll）

## 1.0.1
### 问题
* 优化了一些问题

# 联系方式
```
Email：zhouliuyang1995@163.com
```
