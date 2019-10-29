package com.musheng.android.common.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Author      : FJ
 * CreateDate  : 2019/04/18 09:01
 * Description :
 */

public class ResourceUtils {
    
    private static TypedValue tmpValue = new TypedValue();

    private ResourceUtils(){}

    public static int getXmlDef(Context context, int id){
        synchronized (tmpValue) {
            TypedValue value = tmpValue;
            context.getResources().getValue(id, value, true);
            return (int) TypedValue.complexToFloat(value.data);
        }
    }

    public static int dip2px(Context context, int id) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (getXmlDef(context, id) * scale + 0.5f);
    }

    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

}
