package com.musheng.android.view;

import android.content.Context;
import android.util.AttributeSet;

import com.youth.banner.Banner;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/4 19:41
 * Description :
 *
 */

/*
List<String> images = new ArrayList<>();

images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic1xjab4j30ci08cjrv.jpg");
images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic259ohaj30ci08c74r.jpg");
images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic2b16zuj30ci08cwf4.jpg");
images.add("http://ww4.sinaimg.cn/large/006uZZy8jw1faic2e7vsaj30ci08cglz.jpg");

banner.setImages(images).setImageLoader(new GlideImageLoader()).start();
*/
public class MSBanner extends Banner {

    public MSBanner(Context context) {
        super(context);
    }

    public MSBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MSBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
