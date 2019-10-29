package com.musheng.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.musheng.android.common.util.ResourceUtils;
import com.musheng.android.library.R;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/7 12:37
 * Description :
 */
public class MSRelativeLayout extends RelativeLayout {

    private int backgroundColor;
    private int shapeRadius;
    private int shadowColor;
    private int shadowRadius;
    private int offsetX;
    private int offsetY;
    private boolean hideLeft;
    private boolean hideTop;
    private boolean hideRight;
    private boolean hideBottom;

    public MSRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MSRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MSRelativeLayout, defStyleAttr, defStyleAttr);
        backgroundColor = Color.parseColor("#ffffff");
        shapeRadius = 0;
        shadowColor = Color.parseColor("#25000000");
        shadowRadius = ResourceUtils.dip2px(context, R.dimen.dp_3);
        offsetX = 0;
        offsetY = 0;
        hideLeft = array.getBoolean(R.styleable.MSRelativeLayout_ms_rl_shadow_hide_left, false);
        hideTop = array.getBoolean(R.styleable.MSRelativeLayout_ms_rl_shadow_hide_top, false);
        hideRight = array.getBoolean(R.styleable.MSRelativeLayout_ms_rl_shadow_hide_right, false);
        hideBottom = array.getBoolean(R.styleable.MSRelativeLayout_ms_rl_shadow_hide_bottom, false);

        if(array.hasValue(R.styleable.MSRelativeLayout_ms_rl_shadow_background_color)){
            backgroundColor = array.getColor(R.styleable.MSRelativeLayout_ms_rl_shadow_background_color, backgroundColor);
        }
        if(array.hasValue(R.styleable.MSRelativeLayout_ms_rl_shadow_background_radius)){
            shapeRadius = array.getDimensionPixelSize(R.styleable.MSRelativeLayout_ms_rl_shadow_background_radius, shapeRadius);
        }
        if(array.hasValue(R.styleable.MSRelativeLayout_ms_rl_shadow_color)){
            shadowColor = array.getColor(R.styleable.MSRelativeLayout_ms_rl_shadow_color, shadowColor);
        }
        if(array.hasValue(R.styleable.MSRelativeLayout_ms_rl_shadow_radius)){
            shadowRadius = array.getDimensionPixelSize(R.styleable.MSRelativeLayout_ms_rl_shadow_radius, shadowRadius);
        }
        if(array.hasValue(R.styleable.MSRelativeLayout_ms_rl_shadow_offset_x)){
            offsetX = array.getDimensionPixelSize(R.styleable.MSRelativeLayout_ms_rl_shadow_offset_x, offsetX);
        }
        if(array.hasValue(R.styleable.MSRelativeLayout_ms_rl_shadow_offset_y)){
            offsetY = array.getDimensionPixelSize(R.styleable.MSRelativeLayout_ms_rl_shadow_offset_y, offsetY);
        }
        MSShadowDrawable.setShadowDrawable(this,
                backgroundColor, shapeRadius,
                shadowColor, shadowRadius, offsetX, offsetY,
                hideLeft, hideTop, hideRight, hideBottom);
        array.recycle();
        setPadding(hideLeft ? 0 : shadowRadius,
                hideTop ? 0 :shadowRadius,
                hideRight ? 0 :shadowRadius,
                hideBottom ? 0 : shadowRadius);
    }
    public void setHideTop(boolean isHide){
        hideTop = isHide;
        MSShadowDrawable.setShadowDrawable(this,
                backgroundColor, shapeRadius,
                shadowColor, shadowRadius, offsetX, offsetY,
                hideLeft, hideTop, hideRight, hideBottom);
        setPadding(hideLeft ? 0 : shadowRadius,
                hideTop ? 0 :shadowRadius,
                hideRight ? 0 :shadowRadius,
                hideBottom ? 0 : shadowRadius);
    }
}
