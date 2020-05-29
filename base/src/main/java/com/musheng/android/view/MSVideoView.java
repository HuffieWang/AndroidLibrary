package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Author      : MuSheng
 * CreateDate  : 2020/5/26 17:40
 * Description :
 */
public class MSVideoView extends VideoView {
    public MSVideoView(Context context) {
        super(context);
    }

    public MSVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MSVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}
