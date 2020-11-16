package com.musheng.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.musheng.android.common.glide.GlideRectBitmapTransform;
import com.musheng.android.common.glide.blur.GlideBlurformation;
import com.musheng.android.library.R;

import java.util.List;


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

    public void clear(){
        setBackgroundDrawable(null);
        Glide.with(this).clear(this);
    }

    public void load(String url){
        Glide.with(this).load(url).into(this);
    }

    public void load(Bitmap bitmap){
        Glide.with(this).load(bitmap).into(this);
    }

    public void loadCircle(String url){
        Glide.with(this).load(url).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(this);
    }

    public void loadCircle(Bitmap bitmap){
        Glide.with(this).load(bitmap).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(this);
    }

    public void loadRound(String url, int radius){
        loadRound(url,radius,false);
    }

    public void loadRound(String url, int radius,boolean isFadeIn){
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        if(isFadeIn){
            Glide.with(this).load(url).apply(options).transition(GenericTransitionOptions.<Drawable>with(R.anim.alph_fade_in)).into(this);
        }else{
            Glide.with(this).load(url).apply(options).into(this);
        }
    }

    public void loadRound(Context context,String url, int radius){
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(context).load(url).apply(options).into(this);
    }

    public void loadRound(String url,int leftTopRadius,int rightTopRadius,int leftBottomRadius,int rightBottomRadius){
        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCornersTransform(leftTopRadius,rightTopRadius,rightBottomRadius,leftBottomRadius));
        Glide.with(this).load(url).apply(requestOptions).into(this);
    }

    public void loadRound(String url,int leftTopRadius,int rightTopRadius,int leftBottomRadius,int rightBottomRadius,int errorImg){
        loadRound(url,leftTopRadius,rightTopRadius,leftBottomRadius,rightBottomRadius,errorImg,false);
    }

    public void loadRound(String url,int leftTopRadius,int rightTopRadius,int leftBottomRadius,int rightBottomRadius,int errorImg,boolean isFadeIn){
        RequestOptions requestOptions = new RequestOptions()
                .transform(new RoundedCornersTransform(leftTopRadius,rightTopRadius,rightBottomRadius,leftBottomRadius));
        if(isFadeIn){
            Glide.with(this).load(url).apply(requestOptions).error(errorImg).transition(GenericTransitionOptions.<Drawable>with(R.anim.alph_fade_in)).into(this);
        }else{
            Glide.with(this).load(url).apply(requestOptions).error(errorImg).into(this);
        }
    }

    public void loadVague(Bitmap bitmap){
        Glide.with(this)
                .load(bitmap)
                .apply(RequestOptions.bitmapTransform(new GlideBlurformation(getContext())))
                .into(this);
    }

    public void loadVague(String url){
        Glide.with(this)
                .load(url)
                .apply(RequestOptions.bitmapTransform(new GlideBlurformation(getContext())))
                .into(this);
    }

    public void loadVague(String url,boolean isFadeIn){
        if(isFadeIn){
            Glide.with(this)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new GlideBlurformation(getContext())))
                    .transition(GenericTransitionOptions.<Drawable>with(R.anim.alph_fade_in))
                    .into(this);
        }else{
            Glide.with(this)
                    .load(url)
                    .apply(RequestOptions.bitmapTransform(new GlideBlurformation(getContext())))
                    .into(this);
        }
    }

    public void loadVagurRound(String url,int radius){
        try{
            loadVagurRound(url,radius,false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadVagurRound(String url,int radius,boolean isFadeIn){
        try{
            RoundedCorners roundedCorners = new RoundedCorners(50);
            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
            if(isFadeIn){
                Glide.with(this).load(url).apply(options).apply(RequestOptions.bitmapTransform(new GlideBlurformation(getContext()))).transition(GenericTransitionOptions.<Drawable>with(R.anim.alph_fade_in)).into(this);
            }else{
                Glide.with(this).load(url).apply(options).apply(RequestOptions.bitmapTransform(new GlideBlurformation(getContext()))).into(this);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadRect(String url, List<GlideRectBitmapTransform.GlideRect> rectList){

        Glide.with(this).load(url).apply(RequestOptions.bitmapTransform(
                new GlideRectBitmapTransform(getContext(), rectList)))
                .into(this);
    }

    public void loadGif(int resId){
        Glide.with(this).load(resId).listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                if (resource instanceof GifDrawable) {
                    ((GifDrawable)resource).setLoopCount(-1);
                }
                return false;
            }

        }).into(this);
    }

    public void setSaveEnable(boolean isEnable){
        if(isEnable){
            setDrawingCacheEnabled(true);
            setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    saveImageToGallery(getContext(), getDrawingCache());
                    return true;
                }
            });
        } else {
            setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });
        }
    }

    public void save(){
        saveImageToGallery(getContext(), getDrawingCache());
    }

    public static void saveImageToGallery(Context context, Bitmap bitmap) {
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "title", "description");
        Toast.makeText(context, context.getResources().getString(R.string.photo_save_success), Toast.LENGTH_SHORT).show();
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
