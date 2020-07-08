package com.musheng.android.common.util;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

/**
 * Author      : MuSheng
 * CreateDate  : 2020/4/20 0:58
 * Description :
 */
public class LanguageUtil {
    public static Locale getLocaleByLanguage(String language) {
        Locale locale = Locale.ENGLISH;
        if ("zh".equals(language)) {
            locale = Locale.SIMPLIFIED_CHINESE;
        }else if("tw".equals(language)){
            locale = Locale.TAIWAN;
        }else{
            locale = Locale.ENGLISH;
        }
        return locale;
    }
    public static void changeLanguage(Context context, String language) {
        SharePreferenceUtil.saveString("language", language);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = LanguageUtil.getLocaleByLanguage(language);
        configuration.setLocale(locale);
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);
    }
}
