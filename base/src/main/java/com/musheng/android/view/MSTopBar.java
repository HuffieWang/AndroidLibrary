package com.musheng.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.musheng.android.common.mvp.BaseActivity;
import com.musheng.android.library.R;


/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/4 20:49
 * Description :
 */
public class MSTopBar extends RelativeLayout {

    public static int layoutId = 0;
    public static int backImageResourceId = 0;
    public static int titleTextResourceId = 0;
    public static int cornerTextResourceId = 0;

    private Context context;
    private View contentView;
    private MSTextView cornerText;
    private MSTextView titleText;
    private View backView;

    public MSTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MSTopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr) {

        this.context = context;

        contentView = LayoutInflater.from(context).inflate(getLayoutResourceId(), null);
        MSImageView back = contentView.findViewById(getBackImageResourceId());
        MSTextView title = contentView.findViewById(getTitleTextResourceId());
        titleText = title;
        cornerText = contentView.findViewById(getCornerTextResourceId());

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MSTopBar, defStyleAttr, defStyleAttr);


        if(array.getBoolean(R.styleable.MSTopBar_ms_top_padding_status,true)){
            int compatPadingTop = 0;
            // android 4.4以上将Toolbar添加状态栏高度的上边距，沉浸到状态栏下方
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                compatPadingTop = getStatusBarHeight();
            }
            this.setPadding(getPaddingLeft(), getPaddingTop() + compatPadingTop, getPaddingRight(), getPaddingBottom());
        }

        if(array.hasValue(R.styleable.MSTopBar_ms_top_icon)){
            int id = array.getResourceId(R.styleable.MSTopBar_ms_top_icon, 0);
            if(id != 0){
                back.setImageResource(id);
            } else {
                back.setVisibility(INVISIBLE);
            }
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_title)){
            title.setText(array.getString(R.styleable.MSTopBar_ms_top_title));
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_title_color)){
            title.setTextColor(array.getColor(R.styleable.MSTopBar_ms_top_title_color, Color.parseColor("#333333")));
            cornerText.setTextColor(array.getColor(R.styleable.MSTopBar_ms_top_title_color, Color.parseColor("#333333")));
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_corner)){
            cornerText.setText(array.getString(R.styleable.MSTopBar_ms_top_corner));
            cornerText.setVisibility(VISIBLE);
        }

        boolean hideIcon = array.getBoolean(R.styleable.MSTopBar_ms_top_hide_icon, false);
        back.setVisibility(hideIcon ? INVISIBLE : VISIBLE);
        array.recycle();
        addView(contentView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof BaseActivity){
                    ((BaseActivity) context).finish();
                }
            }
        });

        backView = back;
    }

    public View getBackView() {
        return backView;
    }

    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public void setCornerClickListener(OnClickListener listener){
        cornerText.setOnClickListener(listener);
    }

    public void setTitleText(String text){
        titleText.setText(text);
    }

    public int getLayoutResourceId(){
        if(layoutId == 0){
            return R.layout.view_default_top_bar;
        }
        return layoutId;
    }

    public int getBackImageResourceId(){
        if(backImageResourceId == 0){
            return R.id.iv_left_icon;
        }
        return backImageResourceId;
    }

    public int getTitleTextResourceId(){
        if(titleTextResourceId == 0){
            return R.id.tv_mid_content;
        }
        return titleTextResourceId;
    }

    public int getCornerTextResourceId(){
        if(cornerTextResourceId == 0){
            return R.id.tv_corner;
        }
        return cornerTextResourceId;
    }

    public MSTextView getTitleText() {
        return titleText;
    }

    public View getContentView() {
        return contentView;
    }
}
