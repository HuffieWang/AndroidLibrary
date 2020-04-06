package com.musheng.android.common.log;

import android.util.Log;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/30 10:48
 * Description :
 */
public class MSLog {
    public static void d(String msg){
        Log.d("musheng007", msg);
    }

    public static void e(String msg){
        Log.e("musheng007", msg);
    }

    public static void e(String tag, String msg){
        Log.e("musheng007", tag +  " " + msg);
    }

    public static void d(String tag, String msg){
        Log.d("musheng007", tag +  " " + msg);
    }
}
