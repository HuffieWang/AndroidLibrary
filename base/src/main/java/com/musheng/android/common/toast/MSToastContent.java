package com.musheng.android.common.toast;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 10:34
 * Description :
 */
public abstract class MSToastContent {

    public abstract Context getContext();

    public abstract CharSequence getText();

    public abstract int getDrawableId();

    public Drawable getDrawable(){
        Drawable drawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            drawable = getContext().getDrawable(getDrawableId());
        else
            drawable = getContext().getResources().getDrawable(getDrawableId());
        return drawable;
    }
}
