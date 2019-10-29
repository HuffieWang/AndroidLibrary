package com.musheng.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
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

    public void setGridCount(int count){
        GridLayoutManager layoutManager= new GridLayoutManager(getContext(), count);
        setLayoutManager(layoutManager);
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        super.onChildAttachedToWindow(child);
        if(noneDataView != null){
            noneDataView.setVisibility(GONE);
        }
    }

    public void setNoneDataView(View view) {
        noneDataView = view;
    }

}
