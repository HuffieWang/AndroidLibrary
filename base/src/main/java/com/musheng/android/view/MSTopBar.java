package com.musheng.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.musheng.android.common.mvp.BaseActivity;
import com.musheng.android.library.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;


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
    public static int cornerImageResourceId = 0;

    private Context context;
    private View contentView;
    private MSTextView cornerText;
    private MSTextView titleText;
    private View backView;
    private MSImageView cornerIcon;

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
        cornerIcon = contentView.findViewById(getCornerImageResourceId());
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
        if(array.hasValue(R.styleable.MSTopBar_ms_top_icon_width)){
            ViewGroup.LayoutParams layoutParams = back.getLayoutParams();
            layoutParams.width = (int) array.getDimension(R.styleable.MSTopBar_ms_top_icon_width,WRAP_CONTENT);
            layoutParams.height = (int) array.getDimension(R.styleable.MSTopBar_ms_top_icon_height,WRAP_CONTENT);
            back.setLayoutParams(layoutParams);
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_title)){
            title.setText(array.getString(R.styleable.MSTopBar_ms_top_title));
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_title_bold)){
            if(array.getBoolean(R.styleable.MSTopBar_ms_top_title_bold,false)){
                TextPaint tp = title.getPaint();
                tp.setFakeBoldText(true);
            }
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_title_size)){
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX,array.getDimension(R.styleable.MSTopBar_ms_top_title_size,WRAP_CONTENT));
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_title_color)){
            title.setTextColor(array.getColor(R.styleable.MSTopBar_ms_top_title_color, Color.parseColor("#333333")));
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_corner_color)){
            cornerText.setTextColor(array.getColor(R.styleable.MSTopBar_ms_top_corner_color, Color.parseColor("#333333")));
        }
        if(array.hasValue(R.styleable.MSTopBar_ms_top_corner)){
            cornerText.setText(array.getString(R.styleable.MSTopBar_ms_top_corner));
            cornerText.setVisibility(VISIBLE);
        }

        if(array.hasValue(R.styleable.MSTopBar_ms_top_corner_icon)){
            cornerIcon.setVisibility(VISIBLE);
            int resourceId = array.getResourceId(R.styleable.MSTopBar_ms_top_corner_icon, -1);
            float width = array.getDimension(R.styleable.MSTopBar_ms_top_corner_icon_width, WRAP_CONTENT);
            float height = array.getDimension(R.styleable.MSTopBar_ms_top_corner_icon_height, WRAP_CONTENT);
            if(resourceId != -1){
                cornerIcon.setImageResource(resourceId);
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) width, (int) height);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
            layoutParams.rightMargin = 30;
            cornerIcon.setLayoutParams(layoutParams);
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

    public MSTextView getTitleText() {
        return titleText;
    }

    public View getContentView() {
        return contentView;
    }

    public MSImageView getCornerIcon() {
        return cornerIcon;
    }

    public MSTextView getCornerText() {
        return cornerText;
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

    public void setCornerImageClickListener(OnClickListener listener){
        cornerIcon.setOnClickListener(listener);
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

    public int getCornerImageResourceId(){
        if(cornerImageResourceId == 0){
            return R.id.iv_corner;
        }
        return cornerImageResourceId;
    }


}
