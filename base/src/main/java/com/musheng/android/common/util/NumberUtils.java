package com.musheng.android.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/16 15:45
 * Description :
 */
public class NumberUtils {

    public static int parseInt(String text, int defaultValue){
        try{
            return Integer.valueOf(text);
        }catch (Exception e){
            return defaultValue;
        }
    }

    public static String CointNum(double num, int scale) {
        BigDecimal bigDecimal = new BigDecimal(num);
        return bigDecimal.setScale(scale, RoundingMode.HALF_EVEN).toPlainString();
    }

    public static double parseDouble(String text, double defaultValue){
        try{
            return Double.valueOf(text);
        }catch (Exception e){
            return defaultValue;
        }
    }
}
