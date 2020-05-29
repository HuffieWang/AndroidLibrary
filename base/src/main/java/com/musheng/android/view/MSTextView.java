package com.musheng.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.musheng.android.common.util.SystemUtils;
import com.musheng.android.library.R;


/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/2 17:05
 * Description :
 */
public class MSTextView extends AppCompatTextView {

    private boolean isMedium;

    public MSTextView(Context context) {
        super(context);
    }

    public MSTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public MSTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MSTextView, defStyleAttr, defStyleAttr);
        if(array.hasValue(R.styleable.MSTextView_ms_font)){
            try {
                setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/" + array.getString(R.styleable.MSTextView_ms_font)));
            } catch (Exception ignored){
            }
        }
        if(array.getBoolean(R.styleable.MSTextView_ms_text_marquee, false)){
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
            setMarqueeRepeatLimit(-1);
            setSingleLine(true);
            setSelected(true);
        }
        isMedium = array.getBoolean(R.styleable.MSTextView_ms_text_medium, false);
        array.recycle();
        setIncludeFontPadding(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(isMedium){
            TextPaint paint = getPaint();
            paint.setStrokeWidth(1);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
        }
        super.onDraw(canvas);
    }

    public void setText(int leftIcon, String input){
        String text = "[icon] " + input;
        SpannableString spannable = new SpannableString(text);
        Drawable drawable = this.getResources().getDrawable(leftIcon);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        CenterAlignImageSpan imageSpan = new CenterAlignImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spannable.setSpan(imageSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        setText(spannable);
    }

    public class CenterAlignImageSpan extends ImageSpan {

        public CenterAlignImageSpan(Drawable d) {
            super(d);
        }

        public CenterAlignImageSpan(Drawable d, int verticalAlignment) {
            super(d, verticalAlignment);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom,
                         @NonNull Paint paint) {
            Drawable drawable = getDrawable();
            Paint.FontMetricsInt fm = paint.getFontMetricsInt();
            //计算y方向的位移
            int translationY = (y + fm.descent + y + fm.ascent) / 2 - drawable.getBounds().bottom / 2;
            canvas.save();
            //绘制图片位移一段距离
            canvas.translate(x, translationY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    public void setStrikeThru(boolean isEnable){
        getPaint().setFlags(isEnable ?  Paint. STRIKE_THRU_TEXT_FLAG : 0);
    }

    public void setColorfulText(String... texts){
        if(texts != null && texts.length > 1){
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < texts.length - 1; i += 2){
                String color = texts[i];
                String text = texts[i+1];
                if (TextUtils.isEmpty(color)){
                    builder.append(text);
                } else {
                    builder.append("<font color='").append(color).append("'>").append(text).append("</font>");
                }
            }
            setText(Html.fromHtml(builder.toString()));
        }
    }

    public void enableLongClickCopy(){
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(SystemUtils.copyToClipboard(getContext(), getText().toString())){
                    Toast.makeText(getContext(), "已复制到剪切板", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }
}
