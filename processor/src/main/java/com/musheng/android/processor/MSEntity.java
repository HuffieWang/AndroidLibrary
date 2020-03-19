package com.musheng.android.processor;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 17:40
 * Description :
 */
public @interface MSEntity {
    String name();
    boolean json() default false;
    boolean post() default true;
    boolean keep() default false;
    String dbTable() default "";
    String[] request() default {};
    String[] response();
    String encrypt() default "";
    boolean forceBuildRequest() default false;
    boolean forceBuildResponse() default false;
    boolean forceBuildFetcher() default false;
}
