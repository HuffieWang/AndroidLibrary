package com.musheng.android.common.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.musheng.android.view.MSImageView;
import com.youth.banner.loader.ImageLoader;


public class GlideRoundImageLoader extends ImageLoader {

    private int radius;

    public GlideRoundImageLoader(int radius) {
        this.radius = radius;
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        RoundedCorners roundedCorners = new RoundedCorners(radius);
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners);
        Glide.with(imageView).load(path).apply(options).into(imageView);
    }

}
