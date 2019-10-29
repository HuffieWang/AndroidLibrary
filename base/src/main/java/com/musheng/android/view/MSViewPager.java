package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/3 17:21
 * Description :
 */
public class MSViewPager extends ViewPager {

    public MSViewPager(@NonNull Context context) {
        super(context);
    }

    public MSViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
