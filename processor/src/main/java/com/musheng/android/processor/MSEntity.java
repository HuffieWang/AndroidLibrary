package com.musheng.android.processor;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 17:40
 * Description :
 */
public @interface MSEntity {
    String name();
    boolean post() default true;
    String[] request() default {};
    String[] response();
    boolean forceBuildRequest() default false;
    boolean forceBuildResponse() default false;
    boolean forceBuildFetcher() default false;
}
