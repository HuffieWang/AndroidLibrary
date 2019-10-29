package com.musheng.android.processor;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 15:34
 * Description :
 */
public @interface MSContract {
    String name();
    boolean isFragment() default false;
    String refreshEntity() default "";

    String loadMoreEntity() default "";
    boolean forceView() default false;

    boolean forceRouter() default false;
    boolean forcePresenter() default false;
    boolean forceLayout() default false;
}
