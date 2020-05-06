package com.musheng.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.musheng.android.library.R;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/2 20:02
 * Description :
 */
public class MSRecyclerView extends RecyclerView {

    private View noneDataView;

    private OnFirstVisibleItemChangeListener firstVisibleItemChangeListener;

    public MSRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MSRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        setBackground(null);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MSRecyclerView, defStyleAttr, defStyleAttr);
        boolean horizontal = array.getBoolean(R.styleable.MSRecyclerView_ms_rv_horizontal, false);
        if(horizontal){
            int integer = array.getInteger(R.styleable.MSRecyclerView_ms_rv_horizontal_count, 0);
            if(integer == 0){
                LinearLayoutManager layoutManager= new LinearLayoutManager(context);
                layoutManager.setOrientation(HORIZONTAL);
                setLayoutManager(layoutManager);
            } else {
                GridLayoutManager layoutManager= new GridLayoutManager(context, integer);
                setLayoutManager(layoutManager);
            }
        } else {
            LinearLayoutManager layoutManager= new LinearLayoutManager(context);
            setLayoutManager(layoutManager);
        }
        array.recycle();
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        super.onChildAttachedToWindow(child);
        if(noneDataView != null){
            noneDataView.setVisibility(GONE);
        }
    }

    @Override
    public void onScrollStateChanged(int newState) {
        super.onScrollStateChanged(newState);
        if(firstVisibleItemChangeListener != null){
            int firstPosition = -1;
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                RecyclerView.LayoutManager layoutManager = getLayoutManager();
                if(layoutManager instanceof GridLayoutManager){
                    firstPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                }else if(layoutManager instanceof LinearLayoutManager){
                    firstPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                }
            }
            if(firstPosition != -1){
                firstVisibleItemChangeListener.onFirstItemVisibleChange(firstPosition);
            }
        }
    }

    public void setGridCount(int count){
        GridLayoutManager layoutManager= new GridLayoutManager(getContext(), count);
        setLayoutManager(layoutManager);
    }

    public void setNoneDataView(View view) {
        noneDataView = view;
    }

    public void setOnFirstVisibleItemChangeListener(OnFirstVisibleItemChangeListener listener){
        firstVisibleItemChangeListener = listener;
    }

    public interface OnFirstVisibleItemChangeListener{
        void onFirstItemVisibleChange(int newPosition);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if(isForceIgnoreTouchEvent){
            return false;
        }
        return isForceInterceptTouchEvent || super.onInterceptTouchEvent(e);
    }

    public int getScollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }

    private boolean isForceInterceptTouchEvent = false;
    private boolean isForceIgnoreTouchEvent = false;


    public void setForceInterceptTouchEvent(boolean forceInterceptTouchEvent) {
        isForceInterceptTouchEvent = forceInterceptTouchEvent;
    }

    public void setForceIgnoreTouchEvent(boolean forceIgnoreTouchEvent) {
        isForceIgnoreTouchEvent = forceIgnoreTouchEvent;
    }
}
