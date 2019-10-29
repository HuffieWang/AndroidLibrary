package com.musheng.android.common.util;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/14 16:59
 * Description :
 */
public class SmoothViewHider {

    private int resizeCount;
    private int resizeEndCount;
    private View view;
    private ViewGroup.LayoutParams params;
    private boolean isHiding;

    private int currentCount;
    private Drawable backgroudDrawable;


    public SmoothViewHider(View view) {
        this(view, 20);
    }

    public SmoothViewHider(View view, int resizeCount) {
        this.view = view;
        params = view.getLayoutParams();
        this.resizeCount = resizeCount < 1 ? 1 : resizeCount;
        currentCount = resizeCount;
        backgroudDrawable = view.getBackground();
    }

    public void hide(){
        hide(0.0f);
    }

    public void hide(float percent){
        resizeEndCount = (int)(resizeCount * (1.0f - percent));
        isHiding = true;
        view.setBackground(null);
        resize();
    }

    public void show(){
        isHiding = false;
        resize();
    }

    private void resize(){
        view.post(new Runnable() {
            @Override
            public void run() {
                if(isHiding){
                    if(currentCount >= resizeEndCount){
                        view.setLayoutParams(new LinearLayout.LayoutParams(params.width, params.height*currentCount/ resizeCount));
                        currentCount--;
                        resize();
                    }
                } else  {
                    if(currentCount < resizeCount){
                        view.setLayoutParams(new LinearLayout.LayoutParams(params.width, params.height*currentCount/ resizeCount));
                        currentCount++;
                        resize();
                    } else {
                        view.setBackground(backgroudDrawable);
                    }
                }
            }
        });
    }
}
