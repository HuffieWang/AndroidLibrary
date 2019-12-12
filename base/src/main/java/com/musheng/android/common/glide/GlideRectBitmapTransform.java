package com.musheng.android.common.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.musheng.android.common.log.MSLog;

import java.security.MessageDigest;
import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/12/5 18:39
 * Description :
 */
public class GlideRectBitmapTransform extends BitmapTransformation {

    private List<GlideRect> rectList;
    private Context context;

    public GlideRectBitmapTransform(Context context, List<GlideRect> rectList) {
        this.context = context;
        this.rectList = rectList;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap source, int outWidth, int outHeight) {

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        canvas.drawBitmap(source, 0, 0, paint);
        paint.setColor(Color.parseColor("#80000000"));

        for(GlideRect rect : rectList){

            float startX = calculate(rect.getStartX());
            float startY = calculate(rect.getStartY());
            float stopX = calculate(rect.getStopX());
            float stopY = calculate(rect.getStopY());

            canvas.drawLine(startX, startY, startX,stopY, paint);
            canvas.drawLine(startX, startY, stopX,startY, paint);
            canvas.drawLine(startX, stopY, stopX,stopY, paint);
            canvas.drawLine(stopX, startY, stopX,stopY, paint);
        }
        return result;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        String s = System.currentTimeMillis() + "";
        messageDigest.update(s.getBytes());
    }

    private int calculate(float dpValue){
        return (int) dpValue / 3 * 4;
    }

    public static class GlideRect{
        private float startX;
        private float startY;
        private float stopX;
        private float stopY;

        public GlideRect(float startX, float startY, float stopX, float stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }

        public float getStartX() {
            return startX;
        }

        public void setStartX(float startX) {
            this.startX = startX;
        }

        public float getStartY() {
            return startY;
        }

        public void setStartY(float startY) {
            this.startY = startY;
        }

        public float getStopX() {
            return stopX;
        }

        public void setStopX(float stopX) {
            this.stopX = stopX;
        }

        public float getStopY() {
            return stopY;
        }

        public void setStopY(float stopY) {
            this.stopY = stopY;
        }
    }
}
