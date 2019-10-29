package com.musheng.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.musheng.android.library.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/10/23 18:17
 * Description :
 */
public class MSTile extends LinearLayout {

    private View contentView;
    private MSImageView leftIcon;
    private View leftContentLayout;
    private MSTextView leftContent;
    private MSTextView leftSecondContent;
    private View midContentLayout;
    private MSTextView midContent;
    private MSTextView midSecondContent;
    private MSEditText midInput;
    private MSTextView rightContent;
    private MSImageView rightIcon;
    private View bottomDiv;

    public MSTile(Context context) {
        super(context);
    }

    public MSTile(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MSTile(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_ms_tile, null);

        contentView = view.findViewById(R.id.view_content);
        leftIcon = view.findViewById(R.id.iv_left_icon);
        leftContentLayout = view.findViewById(R.id.view_left_content);
        leftContent = view.findViewById(R.id.tv_left_content);
        leftSecondContent = view.findViewById(R.id.tv_left_second_content);
        midContentLayout = view.findViewById(R.id.view_mid_content);
        midContent = view.findViewById(R.id.tv_mid_content);
        midSecondContent = view.findViewById(R.id.tv_mid_second_content);
        midInput = view.findViewById(R.id.et_input);
        rightContent = view.findViewById(R.id.tv_right_content);
        rightIcon = view.findViewById(R.id.iv_right_icon);
        bottomDiv = view.findViewById(R.id.view_div);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MSTile, defStyleAttr, defStyleAttr);

        float paddingHorizontal = array.getDimension(R.styleable.MSTile_ms_tile_padding_horizontal, 15);
        float paddingVertical = array.getDimension(R.styleable.MSTile_ms_tile_padding_vertical, 5);
        LayoutParams params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        params.setMargins((int)paddingHorizontal, (int)paddingVertical, (int)paddingHorizontal, (int)paddingVertical);
        contentView.setLayoutParams(params);

        if(array.hasValue(R.styleable.MSTile_ms_tile_left_icon) || array.hasValue(R.styleable.MSTile_ms_tile_left_icon_width)
                || array.hasValue(R.styleable.MSTile_ms_tile_left_icon_height)){
            leftIcon.setVisibility(VISIBLE);
            int resourceId = array.getResourceId(R.styleable.MSTile_ms_tile_left_icon, -1);
            float width = array.getDimension(R.styleable.MSTile_ms_tile_left_icon_width, WRAP_CONTENT);
            float height = array.getDimension(R.styleable.MSTile_ms_tile_left_icon_height, WRAP_CONTENT);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_left_icon_margin_end, -1);
            if(resourceId != -1){
                leftIcon.setImageResource(resourceId);
            }
            LayoutParams layoutParams = new LayoutParams((int) width, (int) height);
            if(marginEnd != -1){
                layoutParams.setMarginEnd((int) marginEnd);
            }
            leftIcon.setLayoutParams(layoutParams);
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_left_text)){
            leftContentLayout.setVisibility(VISIBLE);
            leftContent.setVisibility(VISIBLE);
            String content = array.getString(R.styleable.MSTile_ms_tile_left_text);
            int color = array.getColor(R.styleable.MSTile_ms_tile_left_text_color, Color.parseColor("#000000"));
            float size = array.getDimension(R.styleable.MSTile_ms_tile_left_text_size, -1);
            boolean isBold = array.getBoolean(R.styleable.MSTile_ms_tile_left_text_bold, false);
            int background = array.getResourceId(R.styleable.MSTile_ms_tile_left_text_background, -1);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_left_text_margin_end, -1);
            leftContent.setText(content);
            leftContent.setTextColor(color);
            if(size != -1){
                leftContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            if(isBold){
                leftContent.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(background != -1){
                leftContent.setBackgroundResource(background);
            }
            if(marginEnd != -1){
                LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                layoutParams.setMarginEnd((int) marginEnd);
                leftContent.setLayoutParams(layoutParams);
            }
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_left_second_text)){
            leftContentLayout.setVisibility(VISIBLE);
            leftSecondContent.setVisibility(VISIBLE);
            String content = array.getString(R.styleable.MSTile_ms_tile_left_second_text);
            int color = array.getColor(R.styleable.MSTile_ms_tile_left_second_text_color, Color.parseColor("#000000"));
            float size = array.getDimension(R.styleable.MSTile_ms_tile_left_second_text_size, -1);
            boolean isBold = array.getBoolean(R.styleable.MSTile_ms_tile_left_second_text_bold, false);
            int background = array.getResourceId(R.styleable.MSTile_ms_tile_left_second_text_background, -1);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_left_second_text_margin_end, -1);
            leftSecondContent.setText(content);
            leftSecondContent.setTextColor(color);
            if(size != -1){
                leftSecondContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            if(isBold){
                leftSecondContent.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(background != -1){
                leftSecondContent.setBackgroundResource(background);
            }
            if(marginEnd != -1){
                LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                layoutParams.setMarginEnd((int) marginEnd);
                leftSecondContent.setLayoutParams(layoutParams);
            }
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_mid_text)){
            midContentLayout.setVisibility(VISIBLE);
            midContent.setVisibility(VISIBLE);
            String content = array.getString(R.styleable.MSTile_ms_tile_mid_text);
            int color = array.getColor(R.styleable.MSTile_ms_tile_mid_text_color, Color.parseColor("#000000"));
            float size = array.getDimension(R.styleable.MSTile_ms_tile_mid_text_size, -1);
            boolean isBold = array.getBoolean(R.styleable.MSTile_ms_tile_mid_text_bold, false);
            int background = array.getResourceId(R.styleable.MSTile_ms_tile_mid_text_background, -1);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_mid_text_margin_end, -1);
            midContent.setText(content);
            midContent.setTextColor(color);
            if(size != -1){
                midContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            if(isBold){
                midContent.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(background != -1){
                midContent.setBackgroundResource(background);
            }
            if(marginEnd != -1){
                LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                layoutParams.setMarginEnd((int) marginEnd);
                midContent.setLayoutParams(layoutParams);
            }
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_mid_second_text)){
            midContentLayout.setVisibility(VISIBLE);
            midSecondContent.setVisibility(VISIBLE);
            String content = array.getString(R.styleable.MSTile_ms_tile_mid_second_text);
            int color = array.getColor(R.styleable.MSTile_ms_tile_mid_second_text_color, Color.parseColor("#000000"));
            float size = array.getDimension(R.styleable.MSTile_ms_tile_mid_second_text_size, -1);
            boolean isBold = array.getBoolean(R.styleable.MSTile_ms_tile_mid_second_text_bold, false);
            int background = array.getResourceId(R.styleable.MSTile_ms_tile_mid_second_text_background, -1);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_mid_second_text_margin_end, -1);
            midSecondContent.setText(content);
            midSecondContent.setTextColor(color);
            if(size != -1){
                midSecondContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            if(isBold){
                midSecondContent.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(background != -1){
                midSecondContent.setBackgroundResource(background);
            }
            if(marginEnd != -1){
                LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                layoutParams.setMarginEnd((int) marginEnd);
                midSecondContent.setLayoutParams(layoutParams);
            }
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_mid_input) || array.hasValue(R.styleable.MSTile_ms_tile_mid_input_hint)){
            midInput.setVisibility(VISIBLE);
            String content = array.getString(R.styleable.MSTile_ms_tile_mid_input);
            String hint = array.getString(R.styleable.MSTile_ms_tile_mid_input_hint);
            int color = array.getColor(R.styleable.MSTile_ms_tile_mid_input_color, Color.parseColor("#000000"));
            float size = array.getDimension(R.styleable.MSTile_ms_tile_mid_input_size, -1);
            boolean isBold = array.getBoolean(R.styleable.MSTile_ms_tile_mid_input_bold, false);
            int background = array.getResourceId(R.styleable.MSTile_ms_tile_mid_input_background, -1);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_mid_input_margin_end, -1);
            midInput.setText(content);
            midInput.setTextColor(color);
            midInput.setHint(hint);
            if(size != -1){
                midInput.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            if(isBold){
                midInput.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(background != -1){
                midInput.setBackgroundResource(background);
            }
            if(marginEnd != -1){
                LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                layoutParams.setMarginEnd((int) marginEnd);
                midInput.setLayoutParams(layoutParams);
            }
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_right_text)){
            rightContent.setVisibility(VISIBLE);
            String content = array.getString(R.styleable.MSTile_ms_tile_right_text);
            int color = array.getColor(R.styleable.MSTile_ms_tile_right_text_color, Color.parseColor("#000000"));
            float size = array.getDimension(R.styleable.MSTile_ms_tile_right_text_size, -1);
            boolean isBold = array.getBoolean(R.styleable.MSTile_ms_tile_right_text_bold, false);
            int background = array.getResourceId(R.styleable.MSTile_ms_tile_right_text_background, -1);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_right_text_margin_end, -1);
            rightContent.setText(content);
            rightContent.setTextColor(color);
            if(size != -1){
                rightContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
            if(isBold){
                rightContent.setTypeface(Typeface.DEFAULT_BOLD);
            }
            if(background != -1){
                rightContent.setBackgroundResource(background);
            }
            if(marginEnd != -1){
                LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
                layoutParams.setMarginEnd((int) marginEnd);
                rightContent.setLayoutParams(layoutParams);
            }
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_right_icon) || array.hasValue(R.styleable.MSTile_ms_tile_right_icon_width)
                || array.hasValue(R.styleable.MSTile_ms_tile_right_icon_height) ){
            rightIcon.setVisibility(VISIBLE);
            int resourceId = array.getResourceId(R.styleable.MSTile_ms_tile_right_icon, -1);
            float width = array.getDimension(R.styleable.MSTile_ms_tile_right_icon_width, WRAP_CONTENT);
            float height = array.getDimension(R.styleable.MSTile_ms_tile_right_icon_height, WRAP_CONTENT);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_right_icon_margin_end, -1);
            boolean isFill=array.getBoolean(R.styleable.MSTile_ms_title_right_icon_fill,true);
            if(resourceId != -1){
                if(isFill){
                    rightIcon.setBackgroundResource(resourceId);
                }else{
                    rightIcon.setImageResource(resourceId);
                }
            }
            LayoutParams layoutParams = new LayoutParams((int) width, (int) height);
            if(marginEnd != -1){
                layoutParams.setMarginEnd((int) marginEnd);
            }
            rightIcon.setLayoutParams(layoutParams);
        }

        if(array.hasValue(R.styleable.MSTile_ms_tile_bottom_div_height)){
            bottomDiv.setVisibility(VISIBLE);
            float height = array.getDimension(R.styleable.MSTile_ms_tile_bottom_div_height, 1);
            float marginStart = array.getDimension(R.styleable.MSTile_ms_tile_bottom_div_margin_start, 0);
            float marginEnd = array.getDimension(R.styleable.MSTile_ms_tile_bottom_div_margin_end, 0);
            int color = array.getColor(R.styleable.MSTile_ms_tile_bottom_div_color, Color.parseColor("#E4E4E4"));
            LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, (int)height);
            layoutParams.setMarginStart((int) marginStart);
            layoutParams.setMarginEnd((int) marginEnd);
            bottomDiv.setLayoutParams(layoutParams);
            bottomDiv.setBackgroundColor(color);
        }

        addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        array.recycle();
    }

    public MSImageView getLeftIcon() {
        return leftIcon;
    }

    public MSTextView getLeftContent() {
        return leftContent;
    }

    public MSTextView getMidContent() {
        return midContent;
    }

    public MSEditText getMidInput() {
        return midInput;
    }

    public MSTextView getRightContent() {
        return rightContent;
    }

    public MSImageView getRightIcon() {
        return rightIcon;
    }

    public View getContentView() {
        return contentView;
    }

    public View getBottomDiv() {
        return bottomDiv;
    }

    public View getLeftContentLayout() {
        return leftContentLayout;
    }

    public MSTextView getLeftSecondContent() {
        return leftSecondContent;
    }

    public View getMidContentLayout() {
        return midContentLayout;
    }

    public MSTextView getMidSecondContent() {
        return midSecondContent;
    }
}
