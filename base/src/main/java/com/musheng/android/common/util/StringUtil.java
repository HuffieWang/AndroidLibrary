package com.musheng.android.common.util;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/30 14:12
 * Description :
 */
public class StringUtil {
    public static String toUpperCaseFirstOne(String s){
        if(Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
