package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/15 12:33
 * Description :
 */
public class MSHorizontalScrollView extends HorizontalScrollView {

    private float startRawX;
    private float scale;
    private OnPullListener onPullListener;
    private int state;

    public MSHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        scale = context.getResources().getDisplayMetrics().density;
    }

    public MSHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scale = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int state = STATE_NORMAL;

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                if(getChildAt(getChildCount() - 1).getRight() == getScrollX()+getWidth()){
                    if(startRawX == -1) {
                        startRawX = event.getRawX();
                    } else if ((startRawX - event.getRawX()) > 100){
                        state = STATE_PULL_RIGHT;
                    }
                } else if(getScrollX() == 0){
                    if(startRawX == -1) {
                        startRawX = event.getRawX();
                    } else if ((startRawX - event.getRawX()) < -100){
                        state = STATE_PULL_LEFT;
                    }
                } else {
                    startRawX = -1;
                }
                break;

            case MotionEvent.ACTION_UP:
                if(this.state != STATE_NORMAL){
                    if(onPullListener != null){
                        onPullListener.onPull(this.state == STATE_PULL_LEFT);
                    }
                }
                state = STATE_NORMAL;
                startRawX = -1;
                break;
        }
        if(this.state != state){
            this.state = state;
            if(onPullListener != null){
                onPullListener.onStateChange(state);
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnPullListener(OnPullListener listener){
        onPullListener = listener;
    }

    public static final int STATE_NORMAL = 0;
    public static final int STATE_PULL_LEFT = 1;
    public static final int STATE_PULL_RIGHT = 2;

    public interface OnPullListener{
        void onStateChange(int state);
        void onPull(boolean isLeftPull);
    }
}
