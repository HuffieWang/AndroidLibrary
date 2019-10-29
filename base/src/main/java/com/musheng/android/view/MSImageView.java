package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/3 15:56
 * Description :
 */
public class MSImageView extends AppCompatImageView {

    public MSImageView(Context context) {
        super(context);
    }

    public MSImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MSImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void load(String url){
        Glide.with(this).load(url).into(this);
    }

    public void loadCircle(String url){
        Glide.with(this).load(url).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(this);
    }

    public void centerRotate(float fromDegress, float toDegress, long duration, int repeatCount){
        RotateAnimation rotate  = new RotateAnimation(fromDegress, toDegress,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(duration);
        rotate.setRepeatCount(repeatCount);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        setAnimation(rotate);
    }
}
