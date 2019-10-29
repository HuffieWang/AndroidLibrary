package com.musheng.android.processor;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/2 20:23
 * Description :
 */
public @interface MSAdapter {
    String entity();
    String[] views();
    boolean forceBuild() default false;
}
