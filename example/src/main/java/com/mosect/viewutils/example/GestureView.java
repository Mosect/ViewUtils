package com.mosect.viewutils.example;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.mosect.viewutils.GestureHelper;

public class GestureView extends AppCompatTextView {

    private GestureHelper gestureHelper;
    private int gesture;

    public GestureView(Context context) {
        super(context);
        init();
    }

    public GestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gesture = GestureHelper.GESTURE_NONE;
        gestureHelper = GestureHelper.createDefault(getContext());
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureHelper.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
        }
        setGesture(gestureHelper.getGesture());
        return true;
    }

    private void setGesture(int gesture) {
        if (this.gesture != gesture) {
            this.gesture = gesture;
            switch (this.gesture) {
                case GestureHelper.GESTURE_PRESSED:
                    setText("GESTURE_PRESSED");
                    break;
                case GestureHelper.GESTURE_CLICK:
                    setText("GESTURE_CLICK");
                    break;
                case GestureHelper.GESTURE_LONG_CLICK:
                    setText("GESTURE_LONG_CLICK");
                    break;
                case GestureHelper.GESTURE_LEFT:
                    setText("GESTURE_LEFT");
                    break;
                case GestureHelper.GESTURE_UP:
                    setText("GESTURE_UP");
                    break;
                case GestureHelper.GESTURE_RIGHT:
                    setText("GESTURE_RIGHT");
                    break;
                case GestureHelper.GESTURE_DOWN:
                    setText("GESTURE_DOWN");
                    break;
                case GestureHelper.GESTURE_NONE:
                default:
                    setText("");
                    break;
            }
        }
    }
}
